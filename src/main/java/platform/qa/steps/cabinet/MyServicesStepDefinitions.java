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

import io.cucumber.java.uk.Тоді;
import platform.qa.cucumber.TestContext;
import platform.qa.officer.panel.OfficerHeaderPanel;

/**
 * Cucumber step definitions for cabinet portal MyServices page
 */
public class MyServicesStepDefinitions {
    private TestContext testContext;

    public MyServicesStepDefinitions(TestContext testContext) {
        this.testContext = testContext;
    }

    @Тоді("послуга {string} з ідентифікатором {string} відображена в наданих послугах з результатом {string}")
    public void verifyServiceIdentifierDone(String processDefinitionName, String businessKey, String result) {
        new OfficerHeaderPanel().clickOnMyServicesLink()
                .clickOnProvidedServicesTab()
                .checkProcessExistsByNameBusinessKeyAndResult(processDefinitionName, businessKey, result);
    }

    @Тоді("послуга {string} з ідентифікатором {string} знаходиться у статусі: Послуги у виконанні")
    public void verifyServiceIdentifierInProgress(String processDefinitionName, String businessKey) {
        new OfficerHeaderPanel().clickOnMyServicesLink()
                .checkProcessExistsByNameAndBusinessKey(processDefinitionName, businessKey);
    }


}
