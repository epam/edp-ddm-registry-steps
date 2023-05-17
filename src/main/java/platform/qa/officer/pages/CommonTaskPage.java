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

import static java.lang.String.format;
import static org.assertj.core.api.Assertions.assertThat;
import static org.openqa.selenium.By.xpath;
import static org.openqa.selenium.support.ui.ExpectedConditions.elementToBeClickable;
import static org.openqa.selenium.support.ui.ExpectedConditions.visibilityOfElementLocated;

import lombok.SneakyThrows;
import platform.qa.base.BasePage;

import java.util.Arrays;
import org.openqa.selenium.By;

public abstract class CommonTaskPage extends BasePage {
    protected By taskNameBy = xpath("//div[contains(@class, 'root')]//*[self::h1 or self::h2 or self::h3]");
    String buttonXpath = "//button[contains(text(),\"%s\")]";

    public CommonTaskPage() {
        loadingPage();
        loadingComponents();
    }

    @SneakyThrows
    public <T extends CommonTaskPage> T checkTaskName(Class<T> clazz, String... taskNamesExpected) {
        wait.until(visibilityOfElementLocated(taskNameBy));
        assertThat(driver.findElement(taskNameBy).getText()).as("Page title is not in expected list!")
                .isIn(Arrays.asList(taskNamesExpected));
        return (T) Arrays.stream(clazz.getConstructors()).findFirst().orElseThrow().newInstance();
    }

    public void clickButton(String buttonName) {
        wait.until(elementToBeClickable(xpath(format(buttonXpath, buttonName))))
                .click();
    }
}
