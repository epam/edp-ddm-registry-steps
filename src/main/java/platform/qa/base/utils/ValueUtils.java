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

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.log4j.Log4j2;
import platform.qa.cucumber.TestContext;
import platform.qa.entities.context.Request;

import java.util.concurrent.atomic.AtomicReference;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static platform.qa.base.convertors.ContextConvertor.convertToRequestsContext;
import static platform.qa.enums.Context.API_RESULTS;

@Log4j2
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ValueUtils {

    public static String replaceValueFragmentWithValueFromRequest(String businessKey, TestContext testContext) {
        var context = convertToRequestsContext(testContext.getScenarioContext().getContext(API_RESULTS));
        final AtomicReference<String> processedBusinessKey = new AtomicReference<>(businessKey);
        Pattern pattern = Pattern.compile(".*?\\{([\\w]+)}.*?", Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(businessKey);
        while (matcher.find()) {
            String valueKey = matcher.group(1);
            var lastRequest = context.stream()
                    .filter(request -> request.isResultContainsKey(valueKey))
                    .max(Request::compareTo);
            lastRequest.ifPresent(request -> {
                String newValue = businessKey.replace(String.format("{%s}", valueKey), request.getResultValueByKey(valueKey));
                log.debug("The value {} is replaced with {}", businessKey, newValue);
                processedBusinessKey.set(newValue);
            });
        }
        return processedBusinessKey.get();
    }
}