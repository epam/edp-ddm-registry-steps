/*
 * Copyright 2022 EPAM Systems.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package platform.qa.steps.cabinet;

import static org.apache.commons.lang3.StringUtils.substringBetween;
import static platform.qa.base.convertors.ContextConvertor.convertToRandomMapContext;
import static platform.qa.enums.Context.OFFICER_USER_LOGIN;
import static platform.qa.enums.Context.RANDOM_VALUE_MAP;

import io.cucumber.java.DataTableType;
import io.cucumber.java.ParameterType;
import io.cucumber.java.uk.І;
import io.cucumber.java.uk.Коли;
import io.cucumber.java.uk.Та;
import platform.qa.configuration.MasterConfig;
import platform.qa.cucumber.TestContext;
import platform.qa.entities.FieldData;
import platform.qa.enums.FieldType;
import platform.qa.enums.ValueType;
import platform.qa.officer.pages.SignTaskPage;
import platform.qa.officer.pages.TaskPage;
import platform.qa.providers.impl.RegistryUserProvider;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.NoSuchElementException;
import javax.naming.ldap.HasControls;
import org.apache.commons.lang.RandomStringUtils;

/**
 * Cucumber step definitions for cabinet portal different task (forms)
 */
public class TaskStepDefinitions {
    private RegistryUserProvider users = MasterConfig.getInstance().getRegistryConfig().getRegistryUserProvider();
    private TestContext testContext;

    public TaskStepDefinitions(TestContext testContext) {
        this.testContext = testContext;
    }

    @DataTableType
    public FieldData fieldEntry(Map<String, String> entry) {
        return new FieldData(entry.get("name"), getFieldType(entry.get("type")), entry.get("value"));
    }

    private FieldType getFieldType(String entry) {
        try {
            return Arrays.stream(FieldType.values())
                    .filter(value -> value.getType().equals(entry))
                    .findFirst()
                    .orElseThrow();
        } catch (NoSuchElementException ex) {
            return FieldType.valueOf(entry);
        }
    }

    @ParameterType(value = "radiobutton|RADIOBUTTON|checkbox|CHECKBOX|input|INPUT|select|SELECT|datetime|DATETIME")
    public FieldType fieldTypeValue(String value) {
        return getFieldType(value.toUpperCase(Locale.ENGLISH));
    }

    @ParameterType(value = "активна|не активна")
    public boolean booleanValue(String value) {
        return value.equals("активна");
    }

    @Коли("бачить форму {string} із кнопкою {string} яка {booleanValue}")
    public void verifyDisplayFormName(String formName, String buttonName, boolean isEnabled) {
        new TaskPage()
                .checkTaskName(TaskPage.class, formName)
                .checkSubmitButtonState(buttonName, isEnabled);
    }

    @Коли("користувач заповнює форму даними$")
    public void userFillFormFieldsWithData(List<FieldData> rows) {
        var randomValueMap = (HashMap<?,?>) testContext.getScenarioContext().getContext(RANDOM_VALUE_MAP);
        for (FieldData fieldData : rows) {
            if  (fieldData.getValue().startsWith("{")){
                fieldData.setValue((String) randomValueMap.get(substringBetween(fieldData.getValue(), "{", "}")));
            }
            new TaskPage()
                    .setFieldsData(fieldData);
        }
    }

    @Коли("бачить передзаповнені поля із даними$")
    public void userSeesFieldsWithData(List<FieldData> rows) {
        for (FieldData fieldData : rows) {
            new TaskPage()
                    .checkFieldsData(fieldData);
        }
    }

    @Коли("бачить форму/сторінку {string}")
    public void verifyDisplayFormNameWithoutSubmitButton(String formName) {
        new TaskPage()
                .checkTaskName(TaskPage.class, formName);
    }

    @Та("натискає кнопку {string}")
    public void clickButton(String buttonName) {
        new TaskPage()
                .clickButton(buttonName);
    }

    @Та("на формі {string} бачить наступний текст:")
    public void checkFormContent(String formName, String messageText) {
        new TaskPage()
                .checkTaskName(TaskPage.class, formName)
                .checkContentText(messageText);
    }

    @Та("на формі {string} бачить повідомлення {string} з текстом:")
    public void checkMessage(String formName, String messageLabel, String messageText) {
        new TaskPage()
                .checkTaskName(TaskPage.class, formName)
                .checkTextareaText(messageLabel, messageText);
    }

    @Коли("пересвідчується в правильному відображенні введених даних на формі {string}")
    public void checkSignForm(String formName) {
        new TaskPage()
                .checkTaskName(TaskPage.class, formName)
                .checkFieldsAreNotEditable();
    }

    @Коли("підписує дані")
    public void signFormUserFromContext() {
        new SignTaskPage()
                .signTask(users.get((String) testContext.getScenarioContext().getContext(OFFICER_USER_LOGIN)));
    }

    @І("додає запис до {string} таблиці із даними")
    public void userFillGridFieldsWithData(String gridName, List<FieldData> rows) {
        TaskPage taskPage = new TaskPage();
        taskPage
                .clickAddRawEditGridButton(gridName);
        for (FieldData fieldData : rows) {
            taskPage
                    .setFieldsData(fieldData);
        }
        taskPage
                .clickSaveRawEditGridButton();
    }

    @ParameterType(value = "цифр|літер")
    public ValueType randomValueType(String value) {
        return getValueType(value);    }

    private ValueType getValueType(String entry) {
        try {
            return Arrays.stream(ValueType.values())
                    .filter(value -> value.getValueType().equals(entry))
                    .findFirst()
                    .orElseThrow();
        } catch (NoSuchElementException ex) {
            return ValueType.valueOf(entry);
        }
    }

    @І("користувач генерує випадкові дані з {randomValueType} у кількості {int} та записує у змінну {string}")
    public void createRandomValue(String valueType, int amount, String randomValueKey) {
        var randomValueMap = convertToRandomMapContext(testContext.getScenarioContext().getContext(RANDOM_VALUE_MAP));
        String randomValueData;
        switch (getValueType(valueType)){
            case DIGIT:
                randomValueData  = RandomStringUtils.randomNumeric(amount);
                break;
            case LETTER:
                randomValueData  = RandomStringUtils.randomAlphabetic(amount);
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + getValueType(valueType));
        }
        randomValueMap.put(randomValueKey,randomValueData);
        testContext.getScenarioContext().setContext(RANDOM_VALUE_MAP,randomValueMap);
    }
}
