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

package platform.qa.officer.panel;

import platform.qa.base.BasePage;
import platform.qa.officer.pages.LoginPage;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;

public class UserInfoPopUp extends BasePage {

    @FindBy(xpath = "//li[@data-xpath='logoutButton']")
    private WebElement logOutButton;

    public LoginPage clickLogOutButton() {
        wait.until(ExpectedConditions.elementToBeClickable(logOutButton)).click();
        return new LoginPage();
    }
}
