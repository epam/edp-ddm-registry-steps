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

package platform.qa.base.providers;

import io.github.bonigarcia.wdm.WebDriverManager;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.log4j.Log4j2;
import platform.qa.configuration.RunUITestConfiguration;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

/**
 * WebDriver Singleton
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Log4j2
public class WebDriverProvider {
    protected static RunUITestConfiguration runUITestConfig = RunUITestConfiguration.getInstance();
    private static WebDriver instance;

    public static WebDriver getInstance() {
        if (instance == null) {
            instance = init();
        }
        return instance;
    }

    private static WebDriver init() {
        WebDriverManager.chromedriver().setup();
        WebDriver driver;
        driver = getDriver();
        driver.manage().window().maximize();
        return driver;
    }

    private static WebDriver getDriver() {
        if (runUITestConfig.isRemoteRunEnabled()) {
            ChromeOptions options = new ChromeOptions();
            options.setHeadless(true);
            return new ChromeDriver(options);
        } else {
            return new ChromeDriver();
        }
    }

    public static void closeWebDriver() {
        if (instance != null) {
            try {
                instance.quit();
            } catch (Exception exception) {
                log.info("Can not quit browser!!!");
            } finally {
                instance = null;
            }
        }
    }
}
