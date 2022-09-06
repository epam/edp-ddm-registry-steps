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

package platform.qa.steps;

import static platform.qa.enums.Context.OFFICER_USER_LOGIN;

import io.cucumber.java.DataTableType;
import io.cucumber.java.ParameterType;
import io.cucumber.java.uk.І;
import io.cucumber.java.uk.Дано;
import io.cucumber.java.uk.Коли;
import io.cucumber.java.uk.Та;
import io.cucumber.java.uk.Тоді;
import platform.qa.configuration.MasterConfig;
import platform.qa.cucumber.TestContext;
import platform.qa.entities.FieldData;
import platform.qa.enums.FieldType;
import platform.qa.officer.pages.AvailableServicesPage;
import platform.qa.officer.pages.DashboardPage;
import platform.qa.officer.pages.MyTasksPage;
import platform.qa.officer.pages.SignTaskPage;
import platform.qa.officer.pages.TaskPage;
import platform.qa.officer.panel.OfficerHeaderPanel;
import platform.qa.officer.steps.LoginSteps;
import platform.qa.providers.impl.RegistryUserProvider;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.NoSuchElementException;

/**
 * Cucumber step definitions for officer portal
 */
public class OfficerCabinetStepDefinitions {

    private RegistryUserProvider users = MasterConfig.getInstance().getRegistryConfig().getRegistryUserProvider();
    private TestContext testContext;

    public OfficerCabinetStepDefinitions(TestContext testContext) {
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

    @Дано("користувач {string} успішно увійшов у кабінет посадової особи")
    public void verifyOfficerOpenDashboardPage(String userName) {
        new LoginSteps()
                .loginOfficerPortal(users.get(userName));
        testContext.getScenarioContext().setContext(OFFICER_USER_LOGIN, users.get(userName).getLogin());
    }

    @Тоді("користувач {string} вийшов з кабінету посадової особи")
    public void verifyOfficerLogOut(String userName) {
        new LoginSteps()
                .logoutOfficerPortal();
    }

    @Дано("бачить доступний процес {string}")
    public void verifyProcessAvailable(String processName) {
        new DashboardPage()
                .clickOnAvailableServices()
                .checkProcessByName(processName);
    }

    @Коли("користувач ініціює процес {string}")
    public void verifyUserStartProcess(String processName) {
        new AvailableServicesPage()
                .clickOnProcessByName(processName);
    }

    @Коли("бачить форму {string} із кнопкою \"Далі\" яка {booleanValue}")
    public void verifyDisplayFormName(String formName, boolean isEnabled) {
        new TaskPage()
                .checkTaskName(TaskPage.class, formName)
                .checkSubmitButtonState(isEnabled);
    }

    @Коли("користувач заповнює форму даними$")
    public void userFillFormFieldsWithData(List<FieldData> rows) {
        for (FieldData fieldData : rows) {
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

    @Та("натискає кнопку \"Далі\"")
    public void clickButton() {
        new TaskPage()
                .submitForm();
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

    @Тоді("процес закінчено успішно й задача {string} відображається як виконана у переліку задач")
    public void verifyTaskCompleted(String taskName) {
        new MyTasksPage().checkNotificationMessage(taskName)
                .getHeaderPanel().clickOnMyTasksLink()
                .clickOnProvisionedTasksTab()
                .checkTaskExistsByTaskName(taskName);
    }

    @Тоді("послуга {string} з ідентифікатором {string} відображена в наданих послугах")
    public void verifyServiceIdentifierDone(String processDefinitionName, String businessKey) {
        new OfficerHeaderPanel().clickOnMyTasksLink()
                .clickOnProvisionedTasksTab()
                .checkTaskExistsByProcessDefinitionNameAndBusinessKey(processDefinitionName, businessKey);
    }

    @Тоді("послуга {string} з ідентифікатором {string} знаходиться у статусі: Послуги у виконанні")
    public void verifyServiceIdentifierInProgress(String processDefinitionName, String businessKey) {
        new OfficerHeaderPanel().clickOnMyTasksLink()
                .checkTaskExistsByProcessDefinitionNameAndBusinessKey(processDefinitionName, businessKey);
    }

    @Тоді("задача {string} за послугою {string} з ідентифікатором {string} знаходиться у статусі: Задачі для виконання")
    public void verifyTaskServiceIdentifierInProgress(String taskName, String processDefinitionName,
                                                     String businessKey) {
        new OfficerHeaderPanel().clickOnMyTasksLink()
                .checkTaskExistsByProcessBusinessKeyTaskName(processDefinitionName, businessKey, taskName);
    }

    @Тоді("задача {string} за послугою {string} з ідентифікатором {string} виконана")
    public void verifyTaskServiceIdentifierDone(String taskName, String processDefinitionName, String businessKey) {
        new OfficerHeaderPanel().clickOnMyTasksLink()
                .clickOnProvisionedTasksTab()
                .checkTaskExistsByProcessBusinessKeyTaskName(processDefinitionName, businessKey, taskName);
    }

    @Тоді("приймає задачу {string} за послугою {string} з ідентифікатором {string}")
    public void acceptTask(String taskName, String processDefinitionName, String businessKey) {
        new OfficerHeaderPanel().clickOnMyTasksLink()
                .acceptTask(processDefinitionName, businessKey, taskName);
    }

    @Тоді("виконує задачу {string} за послугою {string} з ідентифікатором {string}")
    public void submitTask(String taskName, String processDefinitionName, String businessKey) {
        new OfficerHeaderPanel().clickOnMyTasksLink()
                .submitTask(processDefinitionName, businessKey, taskName);
    }

    @І("додає запис до {string} таблиці із даними")
    public void userFillGridFieldsWithData(String gridName, List<FieldData> rows) {
        TaskPage taskPage = new TaskPage();
        taskPage
                .clickAddButton(gridName);
        for (FieldData fieldData : rows) {
            taskPage
                    .setFieldsData(fieldData);
        }
        taskPage
                .clickSaveButton();
    }
}
