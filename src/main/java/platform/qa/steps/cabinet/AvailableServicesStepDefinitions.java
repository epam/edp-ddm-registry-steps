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

package platform.qa.steps.cabinet;

import io.cucumber.java.uk.Дано;
import io.cucumber.java.uk.Коли;
import platform.qa.cucumber.TestContext;
import platform.qa.officer.pages.AvailableServicesPage;

/**
 * Cucumber step definitions for cabinet portal available services page
 */
public class AvailableServicesStepDefinitions {
    private TestContext testContext;

    public AvailableServicesStepDefinitions(TestContext testContext) {
        this.testContext = testContext;
    }

    @Дано("бачить доступний процес {string}")
    public void verifyProcessAvailable(String processName) {
        new AvailableServicesPage()
                .checkProcessByName(processName);
    }

    @Дано("бачить доступну групу процесів {string}")
    public void verifyProcessGroupAvailable(String processGroupName) {
        new AvailableServicesPage()
                .checkProcessGroupByName(processGroupName);
    }

    @Дано("відкриває групу процесів {string}")
    public void userOpenProcessGroup(String processGroupName){
        new AvailableServicesPage()
                .clickOnProcessGroupByName(processGroupName);
    }

    @Коли("користувач ініціює процес {string}")
    public void userStartProcess(String processName) {
        new AvailableServicesPage()
                .clickOnProcessByName(processName);
    }
}
