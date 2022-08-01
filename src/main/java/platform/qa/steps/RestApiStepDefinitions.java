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

import static java.util.Collections.singletonList;
import static net.javacrumbs.jsonunit.assertj.JsonAssertions.assertThatJson;
import static org.hamcrest.Matchers.in;
import static platform.qa.enums.Context.API_GET_RESULT_MAP_LIST;
import static platform.qa.enums.Context.API_POST_RESULT_MAP_LIST;

import io.cucumber.java.uk.Коли;
import io.cucumber.java.uk.Тоді;
import io.restassured.RestAssured;
import io.restassured.config.LogConfig;
import io.restassured.filter.log.ErrorLoggingFilter;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
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
import java.util.stream.Collectors;
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
        RestAssured.filters(new RequestLoggingFilter(), new ResponseLoggingFilter(), new ErrorLoggingFilter());
        RestAssured.config().logConfig(LogConfig.logConfig()
                .enablePrettyPrinting(Boolean.TRUE));
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
        testContext.getScenarioContext().setContext(API_GET_RESULT_MAP_LIST, getContextWithHistory(Map.of(path, result),
                API_GET_RESULT_MAP_LIST));
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
        testContext.getScenarioContext().setContext(API_GET_RESULT_MAP_LIST, getContextWithHistory(Map.of(path, result),
                API_GET_RESULT_MAP_LIST));
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

        var result = new RestApiClient(registryConfig.getDataFactory(userName), signature)
                .post(payload, path)
                .then()
                .statusCode(201)
                .extract()
                .response()
                .jsonPath()
                .getMap("");

        testContext.getScenarioContext().setContext(API_POST_RESULT_MAP_LIST,
                getContextWithDuplicatesHistory(singletonList(Map.of(path, singletonList(result))),
                        API_POST_RESULT_MAP_LIST));
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
                .delete(id, path + "/");
    }

    @Тоді("користувач {string} чистить дані створені в поточному сценарії запитом {string} за назвою поля {string}")
    public void executeDeleteApiByColumnName(String userName,
                                             @NonNull String path,
                                             @NonNull String idColumnName) {
        List<Map<String, List<Map>>> context =
                (List<Map<String, List<Map>>>) testContext.getScenarioContext().getContext(API_POST_RESULT_MAP_LIST);

        List<String> ids = context.stream()
                .filter(map -> map.containsKey(path))
                .flatMap(stringListMap -> stringListMap.get(path).stream())
                .filter(map -> map.containsKey(idColumnName))
                .map(map -> String.valueOf(map.get(idColumnName)))
                .collect(Collectors.toList());

        ids.forEach(id -> executeDeleteApiWithId(userName, path, id));
    }

    @Тоді("результат запиту {string} містить наступні значення {string} у полі {string}")
    public void verifyApiHasValuesInField(String path, String fieldValue, String fieldName) {
        var actualResult =
                ((Map<String, List<Map>>) testContext.getScenarioContext().getContext(API_GET_RESULT_MAP_LIST)).get(path);
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

    /**
     * Method to replace parameters such as ids with data get from context
     *
     * @param queryParams - parameters for POST or PUT request
     * @return - parameters for ids for POST or PUT request inside body which were replaced by those which were
     * returned previously inside GET requests executed in scenario (get this from context)
     */
    private Map<String, String> getParametersWithIds(Map<String, String> queryParams) {
        //Get results stored from Get requests during scenario run
        Map<String, List<Map>> results =
                (Map<String, List<Map>>) testContext.getScenarioContext().getContext(API_GET_RESULT_MAP_LIST);
        if (results == null || results.isEmpty()) return queryParams;

        Map<String, String> paramsWithIds = new HashMap<>(queryParams);
        queryParams.entrySet().stream()
                .filter(param -> param.getValue() == null)
                .forEach(entry -> results
                        .entrySet()
                        .stream()
                        .flatMap(map -> map.getValue().stream())
                        .filter(result -> result.containsKey(entry.getKey()))
                        .forEach(result -> paramsWithIds.replace(entry.getKey(), result.get(entry.getKey()).toString()))
                );
        return paramsWithIds;
    }

    /**
     * @param result  - Executed API query result which contain tableName as a key and request response as a value
     * @param context - Scenario context name where this data stored in format Map<String, List<Map>>
     * @return - Map<String, List<Map>> with previously data exists in context + new one
     */
    private Map<String, List<Map>> getContextWithHistory(Map<String, List<Map>> result, Context context) {
        Map<String, List<Map>> resultModifiable = new HashMap<>(result);
        Map<String, List<Map>> currentContext =
                (Map<String, List<Map>>) testContext.getScenarioContext().getContext(context);
        if (currentContext != null)
            resultModifiable.putAll(currentContext);
        return resultModifiable;
    }

    /**
     * @param result  - Executed API query result which contain List of tables where can be duplicates by tableName.
     *                tableName as a key and request response as a value
     * @param context - Scenario context name where this data stored in format List<Map<String, List<Map>>>
     * @return - List<Map<String, List<Map>>> with previously data exists in context + new one
     */
    private List<Map<String, List<Map>>> getContextWithDuplicatesHistory(List<Map<String, List<Map>>> result,
                                                                         Context context) {
        List<Map<String, List<Map>>> resultModifiable = new ArrayList<>(result);
        List<Map<String, List<Map>>> currentContext =
                (List<Map<String, List<Map>>>) testContext.getScenarioContext().getContext(context);
        if (currentContext != null)
            resultModifiable.addAll(currentContext);
        return resultModifiable;
    }
}
