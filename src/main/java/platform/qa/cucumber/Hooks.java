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

package platform.qa.cucumber;


import static platform.qa.base.providers.WebDriverProvider.closeWebDriver;
import static platform.qa.base.providers.WebDriverProvider.isWebDriverOpened;

import io.cucumber.java.After;
import io.cucumber.java.Scenario;
import platform.qa.base.providers.WebDriverProvider;
import platform.qa.base.utils.Screenshot;

import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;

public class Hooks {

    @After
    public void closeDriver(Scenario scenario) {
        if (scenario.isFailed() && isWebDriverOpened()) {
            WebDriver driver = WebDriverProvider.getInstance();
            final byte[] screenshot = ((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES);
            scenario.attach(screenshot, "image/png", "screenshot_" + scenario.getName());
            Screenshot.takeScreenshot();
            closeWebDriver();
        }
        if (isWebDriverOpened()) closeWebDriver();
    }
}
