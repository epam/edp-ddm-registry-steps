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

package platform.qa.base;

import lombok.extern.log4j.Log4j2;
import platform.qa.base.providers.WebDriverProvider;
import platform.qa.configuration.MasterConfig;
import platform.qa.configuration.RegistryConfig;

import java.time.Duration;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.WebDriverWait;

/**
 * Common Page Object
 */
@Log4j2
public abstract class BasePage {
    protected static final int DEFAULT_TIMEOUT = 15;
    public static final int LOADER_WAIT_TIMEOUT = 30;
    protected WebDriver driver;
    protected final RegistryConfig registryConfig = MasterConfig.getInstance().getRegistryConfig();
    protected WebDriverWait wait;

    @FindBy(xpath = "//*[@data-xpath='loader']")
    private WebElement loader;
    @FindBy(xpath = "//*[@data-xpath='component-loader']")
    private WebElement componentLoader;

    protected BasePage() {
        this.driver = WebDriverProvider.getInstance();
        this.wait = getDefaultWebDriverWait();
        PageFactory.initElements(driver, this);
    }

    protected WebDriverWait getDefaultWebDriverWait() {
        return new WebDriverWait(driver, Duration.ofSeconds(DEFAULT_TIMEOUT));
    }

    public void openPage(String url) {
        log.info("Trying to open Login Page");
        driver.get(url);
        log.info("Login Page was successfully opened!");
    }

    protected void loadingPage() {
        wait.withTimeout(Duration.ofSeconds(LOADER_WAIT_TIMEOUT)).withMessage("Loader still exist!")
                .until((ExpectedCondition<Boolean>) driver -> {
                    boolean isDisappeared;
                    try {
                        isDisappeared = loader.getCssValue("display").equals("none");
                    } catch (WebDriverException exception) {
                        isDisappeared = true;
                    }
                    return isDisappeared;
                });
        this.wait = getDefaultWebDriverWait();
    }

    protected void loadingComponents() {
        wait.withTimeout(Duration.ofSeconds(LOADER_WAIT_TIMEOUT)).withMessage("Component Loader still exist!")
                .until((ExpectedCondition<Boolean>) driver -> {
                    boolean isDisappeared;
                    try {
                        isDisappeared = componentLoader.getCssValue("display").equals("none");
                    } catch (WebDriverException exception) {
                        isDisappeared = true;
                    }
                    return isDisappeared;
                });
        this.wait = getDefaultWebDriverWait();
    }
}
