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
import static org.hamcrest.Matchers.in;

import io.cucumber.java.uk.Коли;
import io.cucumber.java.uk.Тоді;
import io.restassured.RestAssured;
import io.restassured.parsing.Parser;
import lombok.NonNull;
import lombok.SneakyThrows;
import platform.qa.configuration.MasterConfig;
import platform.qa.configuration.RegistryConfig;
import platform.qa.cucumber.TestContext;
import platform.qa.data.common.SignatureSteps;
import platform.qa.enums.Context;
import platform.qa.rest.RestApiClient;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.http.HttpStatus;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Cucumber step definitions for platform REST API
 */
public class RestApiStepDefinitions {
    private RegistryConfig registryConfig = MasterConfig.getInstance().getRegistryConfig();
    private TestContext testContext;

    public RestApiStepDefinitions(TestContext testContext) {
        this.testContext = testContext;
        RestAssured.defaultParser = Parser.JSON;
        RestAssured.registerParser("text/plain", Parser.JSON);
    }

    @Коли("користувач {string} виконує запит пошуку {string} з параметрами")
    public void executeGetApiWithParameters(String userName,
                                            @NonNull String path,
                                            @NonNull Map<String, String> queryParams) {
        var result = new RestApiClient(registryConfig.getDataFactory(userName))
                .sendGetWithParams(path, queryParams)
                .extract()
                .response()
                .jsonPath()
                .getList("", Map.class);
        testContext.getScenarioContext().setContext(Context.API_RESULT_LIST, getContextWithHistory(result));
    }

    @Коли("користувач {string} виконує запит пошуку {string} без параметрів")
    public void executeGetApiWithoutParameters(String userName,
                                               String path) {
        var result = new RestApiClient(registryConfig.getDataFactory(userName))
                .get(path)
                .then()
                .statusCode(in(getSuccessStatuses()))
                .extract()
                .response()
                .jsonPath()
                .getList("", Map.class);
        testContext.getScenarioContext().setContext(Context.API_RESULT_LIST, getContextWithHistory(result));
    }

    @SneakyThrows
    @Коли("користувач {string} виконує запит створення {string} з тілом запиту")
    public void executePostApiWithParameters(String userName,
                                             @NonNull String path,
                                             @NonNull Map<String, String> queryParams) {
        Map<String, String> paramsWithIds = getParametersWithIds(queryParams);
        String signature = new SignatureSteps(registryConfig.getDataFactory(userName),
                registryConfig.getDigitalSignatureOps(userName),
                registryConfig.getSignatureCeph()).signRequest(paramsWithIds);

        String payload = new ObjectMapper().writeValueAsString(paramsWithIds);

        new RestApiClient(registryConfig.getDataFactory(userName), signature)
                .post(payload, path)
                .then()
                .statusCode(201);
    }

    @SneakyThrows
    @Коли("користувач {string} виконує запит оновлення {string} з ідентифікатором {string} та тілом запиту")
    public void executePutApiWithParameters(String userName,
                                            @NonNull String path,
                                            @NonNull String id,
                                            @NonNull Map<String, String> queryParams) {
        Map<String, String> paramsWithIds = getParametersWithIds(queryParams);
        String signature = new SignatureSteps(registryConfig.getDataFactory(userName),
                registryConfig.getDigitalSignatureOps(userName),
                registryConfig.getSignatureCeph()).signRequest(paramsWithIds);

        String payload = new ObjectMapper().writeValueAsString(paramsWithIds);

        new RestApiClient(registryConfig.getDataFactory(userName), signature)
                .put(id, payload, path);
    }

    @Коли("користувач {string} виконує запит видалення {string} з ідентифікатором {string}")
    public void executeDeleteApiWithId(String userName,
                                       @NonNull String path,
                                       @NonNull String id) {
        String signature = new SignatureSteps(registryConfig.getDataFactory(userName),
                registryConfig.getDigitalSignatureOps(userName),
                registryConfig.getSignatureCeph()).signDeleteRequest(id);

        new RestApiClient(registryConfig.getDataFactory(userName), signature)
                .delete(id, path);
    }

    @Тоді("користувач {string} чистить дані створені в поточному сценарії запитом {string} за назвою поля {string}")
    public void executeDeleteApiByColumnName(String userName,
                                             @NonNull String path,
                                             @NonNull String idColumnName) {
        List<Map> context = (List<Map>) testContext.getScenarioContext().getContext(Context.API_RESULT_LIST);
        var id = String.valueOf(context.stream()
                .filter(map -> map.containsKey(idColumnName))
                .findFirst().orElseThrow()
                .get(idColumnName));

        String signature = new SignatureSteps(registryConfig.getDataFactory(userName),
                registryConfig.getDigitalSignatureOps(userName),
                registryConfig.getSignatureCeph()).signDeleteRequest(id);

        new RestApiClient(registryConfig.getDataFactory(userName), signature)
                .delete(id, path);
    }

    @Тоді("результат запиту містить наступні значення {string} у полі {string}")
    public void verifyApiHasValuesInField(String fieldValue, String fieldName) {
        var actualResult = (List<Map>) testContext.getScenarioContext().getContext(Context.API_RESULT_LIST);
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

    private List<Integer> getSuccessStatuses() {
        return List.of(HttpStatus.SC_OK, HttpStatus.SC_CREATED, HttpStatus.SC_ACCEPTED,
                HttpStatus.SC_NON_AUTHORITATIVE_INFORMATION, HttpStatus.SC_NO_CONTENT, HttpStatus.SC_RESET_CONTENT,
                HttpStatus.SC_PARTIAL_CONTENT, HttpStatus.SC_MULTI_STATUS);
    }

    private Map<String, String> getParametersWithIds(Map<String, String> queryParams) {
        Map<String, String> paramsWithIds = new HashMap<>(queryParams);
        queryParams.entrySet().stream()
                .filter(param -> param.getValue() == null)
                .forEach(entry -> ((List<Map>) testContext.getScenarioContext().getContext(Context.API_RESULT_LIST))
                        .stream()
                        .filter(result -> result.containsKey(entry.getKey()))
                        .forEach(result -> paramsWithIds.replace(entry.getKey(), result.get(entry.getKey()).toString()))
                );
        return paramsWithIds;
    }

    private List<Map> getContextWithHistory(List<Map> result) {
        List<Map> resultModifiable = new ArrayList<>(result);
        List<Map> currentContext = (List<Map>) testContext.getScenarioContext().getContext(Context.API_RESULT_LIST);
        if (currentContext != null)
            resultModifiable.addAll(currentContext);
        return resultModifiable;
    }
}
