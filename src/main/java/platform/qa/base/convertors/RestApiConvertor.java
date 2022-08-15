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

import platform.qa.entities.context.Request;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import com.google.common.base.CaseFormat;

public class RestApiConvertor {

    /**
     * Method to replace parameters such as ids with data get from context
     *
     * @param queryParams - parameters for POST or PUT request
     * @return - parameters with values for such parameters as id got from previous requests results from context
     */
    public static Map<String, String> getParametersWithIds(Map<String, String> queryParams, List<Request> context) {
        if (CollectionUtils.isEmpty(context)) return queryParams;

        Map<String, String> paramsWithIds = new HashMap<>(queryParams);
        queryParams.entrySet().stream()
                .filter(param -> param.getValue() == null)
                .forEach(param -> {
                    var lastRequest = context.stream()
                            .filter(request -> request.isResultContainsKey(param.getKey()))
                            .max(Request::compareTo);
                    lastRequest.ifPresent(request -> paramsWithIds.replace(param.getKey(),
                            String.valueOf(request.getResultValueByKey(param.getKey()))));
                });
        //For array inside parameters
        queryParams.entrySet().stream()
                .filter(param -> param.getValue() != null && param.getValue().startsWith("["))
                .forEach(param -> {
                    var value = StringUtils.substringBetween(param.getValue(), "[", "]");
                    var requests = context.stream()
                            .filter(request -> request.isResultContainsKey(value))
                            .collect(Collectors.toList());
                    if (CollectionUtils.isNotEmpty(requests)) {
                        paramsWithIds.replace(param.getKey(),
                                Arrays.toString(requests.stream()
                                        .map(request -> String.format("\"%s\"", request.getResultValueByKey(value))).toArray()));
                    }
                });
        return paramsWithIds;
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
}
