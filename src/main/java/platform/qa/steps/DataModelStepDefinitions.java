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

import static net.javacrumbs.jsonunit.assertj.JsonAssertions.assertThatJson;
import static net.javacrumbs.jsonunit.core.Option.IGNORING_EXTRA_FIELDS;
import static org.assertj.core.api.Assertions.assertThat;
import static platform.qa.base.convertors.ContextConvertor.convertToRequestsContext;
import static platform.qa.base.utils.CustomFileUtils.getFileNameFromPath;
import static platform.qa.base.utils.CustomFileUtils.getFilePath;
import static platform.qa.base.utils.RequestUtils.getLastRequest;
import static platform.qa.enums.Context.API_RESULTS;

import io.cucumber.java.uk.Дано;
import io.cucumber.java.uk.Тоді;
import platform.qa.base.utils.CustomFileUtils;
import platform.qa.configuration.MasterConfig;
import platform.qa.configuration.RegistryConfig;
import platform.qa.cucumber.TestContext;

import java.text.Collator;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;
import org.assertj.core.api.Assertions;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.sf.json.test.JSONAssert;

/**
 * Cucumber step definitions for data factory search conditions
 */
public class DataModelStepDefinitions {
    private RegistryConfig registryConfig = MasterConfig.getInstance().getRegistryConfig();
    private TestContext testContext;

    public DataModelStepDefinitions(TestContext testContext) {
        this.testContext = testContext;
    }

    @Дано("користувачу {string} доступна розгорнута модель даних з переліком таблиць та згенерованими запитами "
            + "доступу та пошуку даних")
    public void verifyDataFactoryInit(String userName) {
        assertThat(registryConfig.getDataFactory(userName).getUrl())
                .as("Модель даних не розгорнута!!!")
                .isNotNull();
    }

    @Тоді("дата модель за запитом {string} повертає точно заданий json нижче:")
    public void verifyDataModelReturnJsonWithData(String path, String expectedJsonText) {
        var context = convertToRequestsContext(testContext.getScenarioContext().getContext(API_RESULTS));
        var actualResult = getLastRequest(context, path).getResults();
        assertThatJson(actualResult).as("Дані не співпадають:").isEqualTo(expectedJsonText);
    }

    @Тоді("дата модель за запитом {string} повертає json з файлу {string}")
    public void verifyDataModelReturnJsonFromFileWithData(String path, String jsonFilePath) {
        var context = convertToRequestsContext(testContext.getScenarioContext().getContext(API_RESULTS));
        var actualResult = getLastRequest(context, path).getResults();

        String filePath = getFilePath(jsonFilePath);
        String jsonFileName = getFileNameFromPath(jsonFilePath);
        String expectedJsonText = CustomFileUtils.readFromFile(filePath, jsonFileName);
        assertThatJson(actualResult).as("Дані не співпадають:").isEqualTo(expectedJsonText);
    }

    @Тоді("дата модель за запитом {string} повертає json, який містить точно наступні дані, ігноруючі невказані:")
    public void verifyDataModelReturnJsonWithDataFromExpected(String path, String expectedJsonText) {
        var context = convertToRequestsContext(testContext.getScenarioContext().getContext(API_RESULTS));
        var actualResult = getLastRequest(context, path).getResults();
        assertThatJson(actualResult).as("Дані не співпадають:")
                .when(IGNORING_EXTRA_FIELDS).isEqualTo(expectedJsonText);
    }

    @Тоді("дата модель за запитом {string} повертає точно заданий json з файлу {string}, ігноруючі невказані")
    public void verifyDataModelReturnJsonFromFileWithDataFromExpected(String path, String jsonFilePath) {
        var context = convertToRequestsContext(testContext.getScenarioContext().getContext(API_RESULTS));
        var actualResult = getLastRequest(context, path).getResults();

        String filePath = getFilePath(jsonFilePath);
        String jsonFileName = getFileNameFromPath(jsonFilePath);
        String expectedJsonText = CustomFileUtils.readFromFile(filePath, jsonFileName);
        assertThatJson(actualResult).as("Дані не співпадають:")
                .when(IGNORING_EXTRA_FIELDS).isEqualTo(expectedJsonText);
    }

    @Тоді("результат запиту {string} містить наступні значення {string} у полі {string}")
    public void verifyApiHasValuesInField(String path, String fieldValue, String fieldName) {
        var context = convertToRequestsContext(testContext.getScenarioContext().getContext(API_RESULTS));
        var actualResult = getLastRequest(context, path).getResults();

        assertThatJson(actualResult)
                .as("Такого поля не існує в json-і:")
                .inPath("$.." + fieldName)
                .isPresent();
        assertThatJson(actualResult)
                .as("Дані в полі не співпадають:")
                .inPath("$.." + fieldName)
                .isArray()
                .contains(fieldValue);
    }

    @Тоді("дата модель за запитом {string} повертає точно заданий json з файлу {string}, відсортований по полю "
            + "{string} ігноруючі невказані")
    public void verifyDataModelReturnJsonFromFileWithDataFromExpected(String path, String jsonFilePath,
                                                                      String sortingFieldName) throws JsonProcessingException {
        var context = convertToRequestsContext(testContext.getScenarioContext().getContext(API_RESULTS));
        var actualResult = getLastRequest(context, path).getResults();

        List<String> actualSortingFieldValues = new ArrayList<>();
        for (Map map : actualResult) {
            actualSortingFieldValues.add(map.get(sortingFieldName).toString());
        }

        actualSortingFieldValues.sort((o1, o2) -> Collator.getInstance(new Locale("uk", "UA")).compare(o1.toLowerCase(), o2.toLowerCase()));

        Assertions.assertThat(actualSortingFieldValues).as("Дані невірно відсортовані")
                .isEqualTo(actualSortingFieldValues);

        String filePath = getFilePath(jsonFilePath);
        String jsonFileName = getFileNameFromPath(jsonFilePath);
        String expectedJsonText = CustomFileUtils.readFromFile(filePath, jsonFileName);

        ObjectMapper objectMapper = new ObjectMapper();
        List<Map<String, String>> expectedJsonMap = objectMapper.readValue(expectedJsonText,
                new TypeReference<>() {});

        assertThatJson(actualResult.stream().sorted()).as("Дані не співпадають:")
                .when(IGNORING_EXTRA_FIELDS).isEqualTo(expectedJsonMap.stream().sorted());
    }
}
