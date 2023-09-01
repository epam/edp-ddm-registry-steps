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

package platform.qa.base.utils;

import static org.apache.commons.lang3.StringUtils.isNotEmpty;
import static org.apache.commons.lang3.StringUtils.substringBetween;

import platform.qa.base.exceptions.RequestFilterException;
import platform.qa.entities.context.Request;

import java.util.List;
import java.util.Map;

public class RequestUtils {

    public static Request getLastRequest(List<Request> context, String path) {
        return context.stream()
                .filter(request -> request.getName().equals(path))
                .max(Request::compareTo)
                .orElseThrow(() -> new RequestFilterException("Немає запитів зі шляхом:" + path));
    }

    public static boolean hasCurlyBracketsInQueryParameters(Map<String, ?> parametersWithIds) {
        return parametersWithIds.entrySet().stream()
                .anyMatch(entry -> isNotEmpty(substringBetween(String.valueOf(entry.getValue()), "{", "}")));
    }
}
