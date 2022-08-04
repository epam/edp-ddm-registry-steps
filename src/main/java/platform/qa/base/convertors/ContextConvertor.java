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

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ContextConvertor {

    /**
     * @param context - Scenario context to convert
     * @return - converted context to List<Request>
     */
    public static List<Request> convertToRequestsContext(Object context) {
        List<Request> convertedContext = new ArrayList<>();
        if (context instanceof Request) {
            convertedContext = new ArrayList<>() {{
                add((Request) context);
            }};
        }
        if (context instanceof List<?>) {
            convertedContext = ((List<?>) context).stream()
                    .map(item -> (Request) item)
                    .collect(Collectors.toList());
        }
        return convertedContext;
    }
}
