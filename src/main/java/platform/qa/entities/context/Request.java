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

package platform.qa.entities.context;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Data
@AllArgsConstructor
public class Request implements Comparable<Request> {
    private String name;
    private Map parameters;
    private List<Map> results;
    private Timestamp runTimestamp;

    public boolean isResultContainsKey(String keyName) {
        return results.stream().anyMatch(map -> map.containsKey(keyName));
    }

    public boolean isResultContainsKeyWithNonNullValue(String keyName) {
        return results.stream().anyMatch(map -> map.containsKey(keyName) && map.get(keyName) != null);
    }

    public String getResultValueByKey(String keyName) {
        Optional<Map> resultMap = results.stream().filter(map -> map.get(keyName) != null).findFirst();
        return resultMap.map(map -> String.valueOf(map.get(keyName))).orElse(null);
    }

    public void setResultNewValueByKeyValue(String key, String oldValue, String newValue) {
        results.stream()
                .filter(map -> map.get(key).equals(oldValue))
                .map(map -> new HashMap(map))
                .forEach(result -> result.put(key, newValue));
    }

    public List<Map> getResultsContainsMap(Map mapToCheck) {
        return results.stream()
                .filter(map -> ((Map<String, Object>) map).entrySet().stream()
                        .collect(Collectors.toMap(
                                Map.Entry::getKey,
                                entry -> String.valueOf(entry.getValue())))
                        .entrySet()
                        .containsAll(mapToCheck.entrySet()))
                .collect(Collectors.toList());
    }

    @Override
    public int compareTo(Request otherRequest) {
        return getRunTimestamp().compareTo(otherRequest.getRunTimestamp());
    }
}
