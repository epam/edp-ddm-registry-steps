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

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ContextConvertor {

    /**
     * @param context - Scenario context to convert
     * @return - converted context to List<Request>
     */
    public static List<Request> convertToRequestsContext(Object context) {
        List<Request> convertedContext = new ArrayList<>();
        if (context instanceof Request) {
            convertedContext.add((Request) context);
        }
        if (context instanceof List<?>) {
            convertedContext = ((List<?>) context).stream()
                    .map(item -> (Request) item)
                    .collect(Collectors.toList());
        }
        return convertedContext;
    }

    /**
     * @param context - Scenario context to convert
     * @return - converted context to List<File>
     */
    public static List<File> convertToFileList(Object context) {
        List<File> convertedContext = new ArrayList<>();
        if (context instanceof File) {
            convertedContext.add((File) context);
        }
        if (context instanceof List<?>) {
            convertedContext = ((List<?>) context).stream()
                    .map(item -> (File) item)
                    .collect(Collectors.toList());
        }
        return convertedContext;
    }

    /**
     * @param context  - Scenario context to convert
     * @return - converted context to File
     */
    public static File convertToFile(Object context) {
        if (context instanceof File) {
            return (File) context;
        }
        return null;
    }

    /**
     * @param context - Scenario context to convert
     * @return - converted context to List<String>
     */
    public static List<String> convertToStringList(Object context) {
        List<String> convertedContext = new ArrayList<>();
        if (context instanceof String) {
            convertedContext.add((String) context);
        }
        if (context instanceof List<?>) {
            convertedContext = ((List<?>) context).stream()
                    .map(item -> (String) item)
                    .collect(Collectors.toList());
        }
        return convertedContext;
    }

    public static HashMap<String, String> convertToRandomMapContext(Object context) {
        var convertedContext = new HashMap<String, String>();
        if (context instanceof HashMap) {
            var collectedMap = ((Map<?, ?>)context).entrySet().stream()
                    .collect(Collectors.toMap(entry->String.valueOf(entry.getKey()),entry->String.valueOf(entry.getValue())));
            convertedContext.putAll(collectedMap);
            return convertedContext;
        }
        return convertedContext;
    }
}
