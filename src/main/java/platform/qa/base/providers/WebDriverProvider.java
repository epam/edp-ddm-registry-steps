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
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import platform.qa.configuration.RunUITestConfiguration;

import java.io.File;
import java.util.HashMap;
import org.apache.commons.io.FileUtils;
import org.openqa.selenium.Dimension;
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
    public static String chromeDownloadPath = FileUtils.getUserDirectoryPath() + File.separator +"load-chrome";

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
        driver.manage().window().setSize(new Dimension(1920, 1080));
        return driver;
    }

    @SneakyThrows
    private static WebDriver getDriver() {
        HashMap<String, Object> chromePrefs = new HashMap<>();
        FileUtils.forceMkdir(new File(chromeDownloadPath));
        chromePrefs.put("profile.default_content_settings.popups", 0);
        chromePrefs.put("download.default_directory", chromeDownloadPath);
        ChromeOptions options = new ChromeOptions();
        options.setExperimentalOption("prefs", chromePrefs);

        if (runUITestConfig.isRemoteRunEnabled()) {
            options.setHeadless(true);
        }
        return new ChromeDriver(options);
    }

    public static boolean isWebDriverOpened() {
        return instance != null;
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
