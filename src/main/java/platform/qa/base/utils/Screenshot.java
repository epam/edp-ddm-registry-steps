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

package platform.qa.base.utils;

import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import platform.qa.base.providers.WebDriverProvider;

import java.io.File;
import java.net.URI;
import java.nio.file.Paths;
import org.apache.commons.io.FileUtils;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;

@Log4j2
public class Screenshot {
    private static final String SCREENSHOTS_PATH = "target/screenshots";

    @SneakyThrows
    public static void takeScreenshot() {
        WebDriver driver = WebDriverProvider.getInstance();
        File screenshot = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
        URI screenshotPath =
                Paths.get(SCREENSHOTS_PATH, "screenshot_" + System.nanoTime() + ".png").toUri();
        File copy = new File(screenshotPath);
        FileUtils.copyFile(screenshot, copy);
        log.info("Screenshot: " + copy.getAbsolutePath());
    }
}
