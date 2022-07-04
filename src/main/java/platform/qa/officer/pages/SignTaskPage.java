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

package platform.qa.officer.pages;

import platform.qa.entities.Key;
import platform.qa.entities.User;
import platform.qa.officer.pages.components.CesWidget;

public class SignTaskPage extends CommonTaskPage {

    private final CesWidget cesWidget = new CesWidget();

    public SignTaskPage() {
        loadingPage();
        loadingComponents();
    }

    public void signTask(User user) {
        Key key = user.getKey();
        cesWidget.checkReadKeyFormFields().uploadAndReadKey(key.getName(), key.getPassword(), key.getProvider());
        cesWidget.signKey();
    }
}
