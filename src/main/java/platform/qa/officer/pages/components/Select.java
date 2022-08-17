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
import static org.openqa.selenium.By.xpath;
import static org.openqa.selenium.support.ui.ExpectedConditions.elementToBeClickable;
import static org.openqa.selenium.support.ui.ExpectedConditions.invisibilityOf;
import static org.openqa.selenium.support.ui.ExpectedConditions.invisibilityOfAllElements;
import static org.openqa.selenium.support.ui.ExpectedConditions.visibilityOf;

import platform.qa.base.BasePage;

import java.util.List;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedCondition;

public class Select extends BasePage {

    private String selectDropdownButtonPath = "//label[text()[contains(.,\"%s\")"
            + "]]/following-sibling::div//button[@title=\"Open\"]";

    @FindBy(xpath = "//ul[@role='listbox']")
    private WebElement selectTable;

    @FindBy(xpath = "//ul[@role='listbox']/li[@role='option']")
    private List<WebElement> selectItems;

    @FindBy(xpath = "//div[contains(text(),'No options')]")
    private WebElement noItemsIndicator;

    public Select() {
        loadingPage();
        loadingComponents();
    }

    public void selectItemFromDropDown(String itemName, String itemValue) {
        var selectButton = driver.findElement(xpath(format(selectDropdownButtonPath, itemName)));
        wait.withMessage(this::toString).until(elementToBeClickable(selectButton))
                .click();
        waitDropdownLoaded(itemValue);
        var selectItem = getItemByText(itemValue);
        ((ChromeDriver) driver).executeScript("arguments[0].scrollIntoView(true);", selectItem);
        waitDropdownLoaded(itemValue);
        wait.until(elementToBeClickable(getItemByText(itemValue)))
                .click();
    }

    private WebElement getItemByText(String itemValue) {
        return selectItems.stream()
                .filter(item -> item.getText().startsWith(itemValue))
                .findFirst().orElseThrow();
    }

    private void waitDropdownLoaded(String itemValue) {
        wait.until(invisibilityOf(noItemsIndicator));
        wait.until(visibilityOf(selectTable));
        wait.until(invisibilityOfAllElements(selectItems));
        wait.until((ExpectedCondition<Boolean>) driver -> selectItems.stream()
                .anyMatch(item -> item.getText().startsWith(itemValue)));
    }
}