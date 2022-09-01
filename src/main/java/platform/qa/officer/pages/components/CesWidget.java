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

package platform.qa.officer.pages.components;

import static org.openqa.selenium.support.ui.ExpectedConditions.elementToBeClickable;
import static org.openqa.selenium.support.ui.ExpectedConditions.frameToBeAvailableAndSwitchToIt;
import static org.openqa.selenium.support.ui.ExpectedConditions.presenceOfNestedElementLocatedBy;

import platform.qa.base.BasePage;

import java.io.File;
import java.time.Duration;
import org.apache.commons.io.FilenameUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;

public class CesWidget extends BasePage {
    public static final int READ_KEY_TIMEOUT = 60;
    public static final int SIGN_KEY_TIMEOUT = 30;

    @FindBy(xpath = "//iframe[@id='sign-widget']")
    private WebElement signIframe;
    @FindBy(xpath = "//h1[@id='titleLabel']")
    public WebElement signIframeTitle;
    @FindBy(xpath = "//select[@id='pkCASelect']")
    public WebElement providerSelect;
    @FindBy(xpath = "//input[@id='pkReadFileTextField']")
    public WebElement keyInput;
    @FindBy(xpath = "//input[@id='pkReadFileInput']")
    public WebElement readFileInput;
    @FindBy(xpath = "//input[@id='pkReadFilePasswordTextField']")
    public WebElement keyPasswordInput;
    @FindBy(xpath = "//div[@id='pkReadFileButton']")
    public WebElement readKeyButton;
    @FindBy(xpath = "//button[@data-xpath='signButton']")
    public WebElement signKeyButton;

    private static final String PATH_TO_FILE = "src/test/resources/files/";

    public CesWidget() {
        loadingPage();
        loadingComponents();
    }

    public CesWidget uploadCustomKey(String key, String password, String provider) {
        checkReadKeyFormFields();
        uploadAndReadKey(key, password, provider);
        return this;
    }

    public CesWidget checkReadKeyFormFields() {
        wait.until(frameToBeAvailableAndSwitchToIt(signIframe));
        wait.withTimeout(Duration.ofSeconds(READ_KEY_TIMEOUT))
                .until(ExpectedConditions.textToBePresentInElement(signIframeTitle, "Зчитування особистого ключа"));
        wait = getDefaultWebDriverWait();
        wait.until(elementToBeClickable(providerSelect));
        wait.until(elementToBeClickable(keyInput));
        wait.until(ExpectedConditions.attributeToBe(keyPasswordInput, "disabled", "true"));
        wait.until(ExpectedConditions.attributeToBe(readKeyButton, "disabled", "true"));

        return this;
    }

    public void uploadAndReadKey(String key, String password, String provider) {
        wait.until(presenceOfNestedElementLocatedBy(providerSelect, By.tagName("option")));
        new Select(providerSelect).selectByVisibleText(provider);
        uploadKey(key);
        keyPasswordInput.sendKeys(password);
        clickReadKeyButton();
        driver.switchTo().defaultContent();
    }

    public void uploadKey(String key) {
        File file = new File(PATH_TO_FILE.concat("keys/"), FilenameUtils.getName(key));
        readFileInput.sendKeys(file.getAbsolutePath());
    }

    public void clickReadKeyButton() {
        wait.until(elementToBeClickable(readKeyButton))
                .click();
    }

    public void signKey() {
        wait.withTimeout(Duration.ofSeconds(SIGN_KEY_TIMEOUT)).until(elementToBeClickable(signKeyButton))
                .click();
        wait = getDefaultWebDriverWait();
    }
}