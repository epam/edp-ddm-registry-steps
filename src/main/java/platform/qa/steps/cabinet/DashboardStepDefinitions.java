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

import static platform.qa.enums.Context.OFFICER_USER_LOGIN;

import io.cucumber.java.uk.Дано;
import io.cucumber.java.uk.Тоді;
import platform.qa.configuration.MasterConfig;
import platform.qa.cucumber.TestContext;
import platform.qa.officer.steps.LoginSteps;
import platform.qa.providers.impl.RegistryUserProvider;

/**
 * Cucumber step definitions for cabinet portal Dashboard page
 */
public class DashboardStepDefinitions {
    private RegistryUserProvider users = MasterConfig.getInstance().getRegistryConfig().getRegistryUserProvider();
    private TestContext testContext;

    public DashboardStepDefinitions(TestContext testContext) {
        this.testContext = testContext;
    }

    @Дано("користувач {string} успішно увійшов у кабінет посадової особи")
    public void verifyOfficerOpenDashboardPage(String userName) {
        new LoginSteps()
                .loginOfficerPortal(users.get(userName));
        testContext.getScenarioContext().setContext(OFFICER_USER_LOGIN, users.get(userName).getLogin());
    }

    @Тоді("користувач {string} вийшов з кабінету посадової особи")
    public void verifyOfficerLogOut(String userName) {
        new LoginSteps()
                .logoutOfficerPortal();
    }

}
