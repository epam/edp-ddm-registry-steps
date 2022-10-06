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

package platform.qa.base.convertors;

import static org.apache.commons.lang3.StringUtils.isNotEmpty;
import static org.apache.commons.lang3.StringUtils.substringBetween;
import static platform.qa.files.SearchText.searchTextByRegExp;

import platform.qa.entities.context.Request;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;
import org.apache.commons.collections.CollectionUtils;
import com.google.common.base.CaseFormat;

public class RestApiConvertor {

    /**
     * Method to replace parameters such as ids with data get from context
     *
     * @param queryParams - parameters for GET request
     * @return - parameters with values for such parameters as id got from previous requests results from context
     */
    public static Map<String, String> getQueryParamsWithIds(Map<String, String> queryParams, List<Request> context) {
        Map<String, String> paramsWithIds = new HashMap<>(queryParams);

        if (CollectionUtils.isEmpty(context)) return paramsWithIds;

        queryParams.entrySet().stream()
                .filter(param -> isNotEmpty(substringBetween(param.getValue(), "{", "}")))
                .forEach(param -> {
                    var key = substringBetween(param.getValue(), "{", "}");
                    var lastRequest = context.stream()
                            .filter(request -> request.isResultContainsKey(key))
                            .max(Request::compareTo);
                    lastRequest.ifPresent(request -> paramsWithIds.replace(param.getKey(),
                            String.valueOf(request.getResultValueByKey(key))));
                });
        return paramsWithIds;
    }

    /**
     * Method to replace parameters such as ids with data get from context
     *
     * @param queryParams - parameters for POST or PUT request
     * @return - parameters with values for such parameters as id got from previous requests results from context
     */
    public static Map<String, Object> getBodyWithIds(Map<String, String> queryParams, List<Request> context) {
        Map<String, Object> bodyWithIds = new HashMap<>(getQueryParamsWithIds(queryParams, context));

        //For array inside parameters
        queryParams.entrySet().stream()
                .filter(param -> isNotEmpty(substringBetween(param.getValue(), "[", "]")))
                .forEach(param -> {
                    var value = substringBetween(param.getValue(), "[", "]");
                    var requests = context.stream()
                            .filter(request -> request.isResultContainsKey(value))
                            .collect(Collectors.toList());
                    if (CollectionUtils.isNotEmpty(requests)) {
                        bodyWithIds.replace(param.getKey(),
                                requests.stream()
                                        .map(request -> request.getResultValueByKey(value)).collect(Collectors.toList()));
                    }
                });
        return bodyWithIds;
    }

    /**
     * @param path   - Executed API query path
     * @param result - Executed POST API query result which contain Map with key "id" and value
     * @return - Executed POST API query result with replaced Map key from "id" to camelCase {path.concat(id)}
     */
    public static Map getResultKeyConvertedToCamelCase(String path, Map<Object, Object> result) {
        String oldKey = String.valueOf(result.keySet().stream().findFirst().orElseThrow());
        String newKey = CaseFormat.LOWER_HYPHEN.to(CaseFormat.LOWER_CAMEL, String.format("%s-%s", path, oldKey));
        if (!newKey.equals(oldKey)) {
            result.put(newKey, result.get(oldKey));
            result.remove(oldKey);
        }
        return result;
    }

    /**
     * @param path    input request path which can contain id keys without value
     * @param context scenario context where id values stored from previous requests execution
     * @return converted request path where id keys filled with values from context
     */
    public static String getRequestPathWithIds(String path, List<Request> context) {
        if (path.matches(".*\\{\\w+}.*")) {
            AtomicReference<String> newPath = new AtomicReference<>(path);
            String key = searchTextByRegExp(path, "(?<=\\{)\\w+?(?=\\})");
            Optional<Request> lastRequest = context.stream()
                    .filter(request -> request.isResultContainsKey(key) && request.getResultValueByKey(key) != null)
                    .max(Request::compareTo);
            lastRequest.ifPresent(request -> newPath.set(path.replaceAll("\\{\\w+}",
                    request.getResultValueByKey(key))));
            return newPath.get();
        }
        return path;
    }

    /**
     * @param responseObject request response object
     * @return converted response object to List<Map>
     */
    public static List<Map> convertToListMap(Object responseObject) {
        List<Map> convertedResponse = new ArrayList<>();
        if (responseObject instanceof Map) {
            convertedResponse.add((Map) responseObject);
        }
        if (responseObject instanceof List<?>) {
            convertedResponse = ((List<?>) responseObject).stream()
                    .map(item -> (Map) item)
                    .collect(Collectors.toList());
        }
        return convertedResponse;
    }
}

