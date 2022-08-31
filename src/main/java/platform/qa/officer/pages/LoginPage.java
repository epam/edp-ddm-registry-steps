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

import static org.openqa.selenium.support.ui.ExpectedConditions.elementToBeClickable;
import static org.openqa.selenium.support.ui.ExpectedConditions.urlToBe;
import static org.openqa.selenium.support.ui.ExpectedConditions.visibilityOf;

import lombok.extern.log4j.Log4j2;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

@Log4j2
public class LoginPage extends OfficerBasePage {

    @FindBy(xpath = "//button[@data-xpath='loginButton']")
    WebElement authButton;

    public AuthWithCesPage openAuthWithCesPage() {
        return openPage()
                .checkThatAuthorizationButtonIsActive()
                .clickAuthorizationButton();
    }

    public LoginPage openPage() {
        openPage(baseUrl);
        wait
                .until(urlToBe(getExpectedLoginUrl()));
        return this;
    }

    public AuthWithCesPage clickAuthorizationButton() {
        wait
                .until(visibilityOf(authButton))
                .click();
        return new AuthWithCesPage();
    }

    public LoginPage checkThatAuthorizationButtonIsActive() {
        wait.until(elementToBeClickable(authButton));
        return this;
    }

    private String getExpectedLoginUrl() {
        if (baseUrl.lastIndexOf("/") < 0) return baseUrl + "/officer/login";
        if (baseUrl.endsWith("/") && !baseUrl.endsWith("officer/")) return baseUrl + "officer/login";
        if (baseUrl.endsWith("officer")) return baseUrl + "/login";
        if (baseUrl.endsWith("officer/")) return baseUrl + "login";
        return baseUrl;
    }
}
