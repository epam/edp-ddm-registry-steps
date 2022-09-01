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

package platform.qa.officer.steps;

import platform.qa.entities.Key;
import platform.qa.entities.User;
import platform.qa.officer.pages.DashboardPage;
import platform.qa.officer.pages.LoginPage;

/**
 * Aggregated steps for login
 */
public class LoginSteps {
    public DashboardPage loginOfficerPortal(User user) {
        Key userKey = user.getKey();
        return new LoginPage()
                .openAuthWithCesPage()
                .readAndSignKey(userKey.getName(), userKey.getPassword(), userKey.getProvider());
    }
}
