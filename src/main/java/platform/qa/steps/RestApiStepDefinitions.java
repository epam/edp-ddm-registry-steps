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

import static java.util.Collections.reverseOrder;
import static java.util.Collections.singletonList;
import static org.hamcrest.Matchers.in;
import static platform.qa.enums.Context.API_RESULTS_UNIQUE;
import static platform.qa.enums.Context.API_RESULTS_WITH_DUPLICATES;

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
import platform.qa.enums.Context;
import platform.qa.rest.RestApiClient;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpStatus;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.CaseFormat;

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
        Map<String, String> paramsWithIds = getParametersWithIds(queryParams);
        if (paramsWithIds.containsValue(null)) return;
        var result = new RestApiClient(registryConfig.getDataFactory(userName))
                .sendGetWithParams(path, paramsWithIds)
                .extract()
                .response()
                .jsonPath()
                .getList("", Map.class);

        testContext.getScenarioContext().setContext(API_RESULTS_UNIQUE, Map.of(path, result));

        testContext.getScenarioContext().setContext(API_RESULTS_WITH_DUPLICATES,
                getContextWithHistory(Map.of(path, result), API_RESULTS_WITH_DUPLICATES));
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

        testContext.getScenarioContext().setContext(API_RESULTS_UNIQUE, Map.of(path, result));

        testContext.getScenarioContext().setContext(API_RESULTS_WITH_DUPLICATES,
                getContextWithHistory(Map.of(path, result), API_RESULTS_WITH_DUPLICATES));
    }

    @Коли("виконується фільтрація результатів запиту {string} за параметрами")
    public void filterApiResultsByParams(@NonNull String path, @NonNull Map<String, String> params) {
        var context =
                (LinkedHashMap<String, List<Map>>) testContext.getScenarioContext().getContext(API_RESULTS_WITH_DUPLICATES);
        LinkedHashMap<String, List<Map>> filteredResult = new LinkedHashMap<>();

        if (MapUtils.isNotEmpty(context) && CollectionUtils.isNotEmpty(context.get(path))) {
            filteredResult.put(path,
                    context.get(path).stream()
                            .filter(contextMap -> contextMap.entrySet().containsAll(params.entrySet()))
                            .collect(Collectors.toList())
            );

            filteredResult.putAll(context.entrySet().stream()
                    .filter(entry -> !entry.getKey().equals(path))
                    .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue)));

            log.info("Filtered Results are: \n" + StringUtils.join(filteredResult));
            testContext.getScenarioContext().setContext(API_RESULTS_WITH_DUPLICATES, filteredResult);
        }
    }

    @SneakyThrows
    @Коли("користувач {string} виконує запит створення {string} з тілом запиту")
    public void executePostApiWithParameters(String userName,
                                             @NonNull String path,
                                             @NonNull Map<String, String> queryParams) {
        Map<String, String> paramsWithIds = getParametersWithIds(queryParams);
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

        Map resultWithUpdatedKeyName = getResultWithIdNameToCamelCase(path, new HashMap<>(result));

        testContext.getScenarioContext().setContext(API_RESULTS_UNIQUE, Map.of(path, resultWithUpdatedKeyName));

        testContext.getScenarioContext().setContext(API_RESULTS_WITH_DUPLICATES, getContextWithHistory(Map.of(path,
                singletonList(resultWithUpdatedKeyName)), API_RESULTS_WITH_DUPLICATES));
    }

    @SneakyThrows
    @Коли("користувач {string} виконує запит оновлення {string} з ідентифікатором {string} та тілом запиту")
    public void executePutApiWithParameters(String userName,
                                            @NonNull String path,
                                            @NonNull String id,
                                            @NonNull Map<String, String> queryParams) {
        Map<String, String> paramsWithIds = getParametersWithIds(queryParams);
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
        Map<String, List<Map>> context =
                (Map<String, List<Map>>) testContext.getScenarioContext().getContext(API_RESULTS_WITH_DUPLICATES);

        List<String> ids = context.entrySet().stream()
                .flatMap(stringListEntry -> stringListEntry.getValue().stream())
                .filter(map -> map.containsKey(idColumnName))
                .map(map -> String.valueOf(map.get(idColumnName)))
                .distinct()
                .collect(Collectors.toList());

        ids.forEach(id -> executeDeleteApiWithId(userName, path, id));
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
        LinkedHashMap<String, List<Map>> results =
                (LinkedHashMap<String, List<Map>>) testContext.getScenarioContext().getContext(API_RESULTS_WITH_DUPLICATES);

        if (MapUtils.isEmpty(results)) return queryParams;

        Map<String, String> paramsWithIds = new HashMap<>(queryParams);
        queryParams.entrySet().stream()
                .filter(param -> param.getValue() == null)
                .forEach(entry -> results
                        .forEach((key, value) -> value.stream()
                                .filter(result -> result.containsKey(entry.getKey()) && result.get(entry.getKey()) != null)
                                .forEach(map -> paramsWithIds.replace(entry.getKey(),
                                        String.valueOf(map.get(entry.getKey()))))));
        return paramsWithIds;
    }

    /**
     * @param result  - Executed API query result which contain List of tables where can be duplicates by tableName.
     *                tableName as a key and request response as a value
     * @param context - Scenario context name where this data stored in format Map<String, List<Map>>
     * @return - Map<String, List<Map>> with previously data exists in context + new one
     */
    private LinkedHashMap<String, LinkedList<Map>> getContextWithHistory(Map<String, List<Map>> result,
                                                                         Context context) {
        LinkedHashMap<String, LinkedList<Map>> resultModifiable = new LinkedHashMap(result);
        LinkedHashMap<String, List<Map>> currentContext =
                (LinkedHashMap<String, List<Map>>) testContext.getScenarioContext().getContext(context);
        if (MapUtils.isNotEmpty(currentContext)) {
            return new LinkedHashMap<>(Stream.concat(resultModifiable.entrySet().stream(),
                            currentContext.entrySet().stream())
                    .collect(Collectors.toMap(entry -> String.valueOf(entry.getKey()),
                            entry -> new LinkedList<>(entry.getValue()),
                            (v1ResultList, v2ResultList) -> {
                                v1ResultList.addAll(v2ResultList);
                                v1ResultList.sort(reverseOrder());
                                return v1ResultList;
                            })));
        }
        return resultModifiable;
    }

    /**
     * @param path   - Executed API query path
     * @param result - Executed POST API query result which contain Map with key "id" and value
     * @return - Executed POST API query result with replaced Map key from "id" to camelCase {path.concat(id)}
     */
    private Map getResultWithIdNameToCamelCase(String path, Map<Object, Object> result) {
        String oldKey = String.valueOf(result.keySet().stream().findFirst().orElseThrow());
        String newKey = CaseFormat.LOWER_HYPHEN.to(CaseFormat.LOWER_CAMEL, String.format("%s-%s", path, oldKey));
        if (!newKey.equals(oldKey)) {
            result.put(newKey, result.get(oldKey));
            result.remove(oldKey);
        }
        return result;
    }
}
