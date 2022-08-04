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

import static java.lang.System.currentTimeMillis;
import static java.util.Collections.singletonList;
import static org.hamcrest.Matchers.in;
import static platform.qa.base.convertors.ContextConvertor.convertToRequestsContext;
import static platform.qa.base.convertors.RestApiConvertor.getParametersWithIds;
import static platform.qa.base.convertors.RestApiConvertor.getResultKeyConvertedToCamelCase;
import static platform.qa.base.utils.RequestUtils.getLastRequest;
import static platform.qa.enums.Context.API_RESULTS;

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
import lombok.extern.log4j.Log4j2;
import platform.qa.configuration.MasterConfig;
import platform.qa.configuration.RegistryConfig;
import platform.qa.cucumber.TestContext;
import platform.qa.data.common.SignatureSteps;
import platform.qa.entities.context.Request;
import platform.qa.rest.RestApiClient;

import java.sql.Timestamp;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpStatus;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Cucumber step definitions for platform REST API
 */
@Log4j2
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
        var context = convertToRequestsContext(testContext.getScenarioContext().getContext(API_RESULTS));
        Map<String, String> paramsWithIds = getParametersWithIds(queryParams, context);
        if (paramsWithIds.containsValue(null)) return;

        var result = new RestApiClient(registryConfig.getDataFactory(userName))
                .sendGetWithParams(path, paramsWithIds)
                .extract()
                .response()
                .jsonPath()
                .getList("", Map.class);
        var request = new Request(path, queryParams, result, new Timestamp(currentTimeMillis()));
        context.add(request);

        testContext.getScenarioContext().setContext(API_RESULTS, context);
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

        var context = convertToRequestsContext(testContext.getScenarioContext().getContext(API_RESULTS));
        var request = new Request(path, Collections.emptyMap(), result, new Timestamp(currentTimeMillis()));
        context.add(request);

        testContext.getScenarioContext().setContext(API_RESULTS, context);
    }

    @Коли("виконується фільтрація результатів запиту {string} за параметрами")
    public void filterApiResultsByParams(@NonNull String path, @NonNull Map<String, String> params) {
        var context = convertToRequestsContext(testContext.getScenarioContext().getContext(API_RESULTS));
        var filteredContext = getLastRequest(context, path).getResultsContainsMap(params);
        getLastRequest(context, path).setResults(filteredContext);

        log.info("Відфільтровані результати наступні: \n" + StringUtils.join(filteredContext, "\n"));
        testContext.getScenarioContext().setContext(API_RESULTS, context);
    }

    @SneakyThrows
    @Коли("користувач {string} виконує запит створення {string} з тілом запиту")
    public void executePostApiWithParameters(String userName,
                                             @NonNull String path,
                                             @NonNull Map<String, String> queryParams) {
        var context = convertToRequestsContext(testContext.getScenarioContext().getContext(API_RESULTS));
        Map<String, String> paramsWithIds = getParametersWithIds(queryParams, context);

        if (paramsWithIds.containsValue(null)) return;

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

        var updatedResult = getResultKeyConvertedToCamelCase(path, new HashMap<>(result));
        var request = new Request(path, queryParams, singletonList(updatedResult), new Timestamp(currentTimeMillis()));
        context.add(request);

        testContext.getScenarioContext().setContext(API_RESULTS, context);
    }

    @SneakyThrows
    @Коли("користувач {string} виконує запит оновлення {string} з ідентифікатором {string} та тілом запиту")
    public void executePutApiWithParameters(String userName,
                                            @NonNull String path,
                                            @NonNull String id,
                                            @NonNull Map<String, String> queryParams) {
        var context = convertToRequestsContext(testContext.getScenarioContext().getContext(API_RESULTS));
        Map<String, String> paramsWithIds = getParametersWithIds(queryParams, context);

        if (paramsWithIds.containsValue(null)) return;

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

    @Тоді("користувач {string} виконує запит {string} видалення даних створених в сценарії з назвою параметру {string}")
    public void executeDeleteApiByColumnName(String userName,
                                             @NonNull String path,
                                             @NonNull String idColumnName) {
        var context = convertToRequestsContext(testContext.getScenarioContext().getContext(API_RESULTS));

        var filteredRequests = context.stream()
                .filter(request -> request.isResultContainsKeyWithNonNullValue(idColumnName))
                .collect(Collectors.toList());

        filteredRequests.stream()
                .map(request -> request.getResultValueByKey(idColumnName))
                .forEach(id -> executeDeleteApiWithId(userName, path, id));

        context.removeAll(filteredRequests);
        testContext.getScenarioContext().setContext(API_RESULTS, context);
    }

    private List<Integer> getSuccessStatuses() {
        return List.of(HttpStatus.SC_OK, HttpStatus.SC_CREATED, HttpStatus.SC_ACCEPTED,
                HttpStatus.SC_NON_AUTHORITATIVE_INFORMATION, HttpStatus.SC_NO_CONTENT, HttpStatus.SC_RESET_CONTENT,
                HttpStatus.SC_PARTIAL_CONTENT, HttpStatus.SC_MULTI_STATUS);
    }
}
