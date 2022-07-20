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

import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import platform.qa.base.BasePage;

import java.util.Objects;

import static java.lang.String.format;
import static org.openqa.selenium.By.xpath;
import static org.openqa.selenium.support.ui.ExpectedConditions.presenceOfAllElementsLocatedBy;
import static org.openqa.selenium.support.ui.ExpectedConditions.visibilityOfAllElements;

public class Select extends BasePage {

    private final String selectDropdownPath = "//label[text()[contains(.,\"%s\")"
            + "]]/following-sibling::div//input[@type='text']";

    private final String selectItems = "//ul[contains(@class,'MuiAutocomplete-listbox')]/li";

    public Select() {
        loadingPage();
        loadingComponents();
    }

    public void selectItemFromDropDown(String itemName, String itemValue) {
        String selectXPath = getSelectXPath(itemName);
        WebElement select = driver.findElement(xpath(selectXPath));
        wait.until(ExpectedConditions.elementToBeClickable(select))
                .click();
        wait.until(presenceOfAllElementsLocatedBy(xpath(selectItems)));
        wait.until(visibilityOfAllElements(driver.findElements(xpath(selectItems))));
        wait.until((ExpectedCondition<Boolean>) driver -> Objects.requireNonNull(driver)
                .findElements(xpath(selectItems)).stream()
                .noneMatch(item -> item.getText().isEmpty()));
        WebElement element = driver.findElements(xpath(selectItems)).stream()
                .filter(item -> item.getText().startsWith(itemValue))
                .findFirst().orElseThrow();
        ((ChromeDriver) driver).executeScript("arguments[0].scrollIntoView(true);", element);
        element.click();
        wait.until((ExpectedCondition<Boolean>) driver -> !select.getAttribute("value").isEmpty());
    }

    public String getSelectXPath(String itemName) {
        return format(selectDropdownPath, itemName);
    }
}
