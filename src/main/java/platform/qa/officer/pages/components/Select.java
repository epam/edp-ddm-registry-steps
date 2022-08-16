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

import static java.lang.String.format;
import static java.util.Objects.requireNonNull;
import static org.openqa.selenium.By.xpath;
import static org.openqa.selenium.support.ui.ExpectedConditions.presenceOfAllElementsLocatedBy;
import static org.openqa.selenium.support.ui.ExpectedConditions.visibilityOfAllElements;

import lombok.SneakyThrows;
import platform.qa.base.BasePage;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;

public class Select extends BasePage {

    private final String selectDropdownButtonPath = "//label[text()[contains(.,\"%s\")"
            + "]]/following-sibling::div//button[@title=\"Open\"]";
    private final String selectDropdownInputPath = "//label[text()[contains(.,\"%s\")"
            + "]]/following-sibling::div//input[@type='text']";

    private final String selectItems = "//ul[contains(@class,'MuiAutocomplete-listbox')]/li";

    public Select() {
        loadingPage();
        loadingComponents();
    }

    @SneakyThrows
    public void selectItemFromDropDown(String itemName, String itemValue) {
        String selectButtonXPath = format(selectDropdownButtonPath, itemName);
        WebElement selectButton = driver.findElement(xpath(selectButtonXPath));
        wait.until(ExpectedConditions.elementToBeClickable(selectButton))
                .click();
        waitDropdownLoaded(itemValue);
        WebElement selectItem = getElementByStartText(itemValue);
        ((ChromeDriver) driver).executeScript("arguments[0].scrollIntoView(true);", selectItem);
        wait.until(ExpectedConditions.elementToBeClickable(selectItem))
                .click();
        checkValueSelected(itemName, itemValue);
    }

    private WebElement getElementByStartText(String itemValue) {
        return driver.findElements(xpath(selectItems)).stream()
                .filter(item -> item.getText().startsWith(itemValue))
                .findFirst().orElseThrow();
    }

    private void waitDropdownLoaded(String itemValue) {
        wait.until(presenceOfAllElementsLocatedBy(xpath(selectItems)));
        wait.until(visibilityOfAllElements(driver.findElements(xpath(selectItems))));
        wait.until((ExpectedCondition<Boolean>) driver -> requireNonNull(driver)
                .findElements(xpath(selectItems)).stream()
                .anyMatch(item -> item.getText().startsWith(itemValue)));
    }

    private void checkValueSelected(String itemName, String itemValue) {
        String selectInputXPath = getSelectInputXPath(itemName);
        wait.until((ExpectedCondition<Boolean>) driver -> requireNonNull(driver)
                .findElement(xpath(selectInputXPath))
                .getText().startsWith(itemValue));
    }

    public String getSelectInputXPath(String itemName) {
        return format(selectDropdownInputPath, itemName);
    }
}
