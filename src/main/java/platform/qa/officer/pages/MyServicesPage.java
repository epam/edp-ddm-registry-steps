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

import lombok.Getter;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import platform.qa.cucumber.TestContext;
import platform.qa.officer.pages.components.Row;
import platform.qa.officer.pages.components.Table;
import platform.qa.officer.panel.OfficerHeaderPanel;

import static org.openqa.selenium.support.ui.ExpectedConditions.elementToBeClickable;
import static org.openqa.selenium.support.ui.ExpectedConditions.textToBePresentInElement;
import static platform.qa.base.utils.ValueUtils.replaceValueFragmentWithValueFromRequest;

public class MyServicesPage extends OfficerBasePage {

    @Getter
    private final OfficerHeaderPanel headerPanel = new OfficerHeaderPanel();

    private final String myServicesTextUa = "Мої послуги";
    @FindBy(xpath = "//div[@data-xpath='header']/following-sibling::div//h1")
    private WebElement myServicesHeader;
    @FindBy(xpath = "//button[@data-xpath='tabsButton-ended']")
    private WebElement servicesProvidedTab;

    public MyServicesPage() {
        loadingPage();
        loadingComponents();
        checkMyServicesHeader();
    }

    public MyServicesPage checkMyServicesHeader() {
        wait.until(textToBePresentInElement(myServicesHeader, myServicesTextUa));
        return this;
    }

    public MyServicesPage clickOnProvidedServicesTab() {
        wait.until(elementToBeClickable(servicesProvidedTab)).click();
        return new MyServicesPage();
    }

    public MyServicesPage checkProcessExistsByNameBusinessKeyAndResult(String processDefinitionName,
            String businessKey, String result, TestContext testContext) {
        new Table().getLastRowFromTableByProcessBusinessKeyAndResult(processDefinitionName, businessKey, result, testContext);
        return this;
    }

    public MyServicesPage checkProcessExistsByNameAndBusinessKey(String processDefinitionName, String businessKey, TestContext testContext) {
        new Table().getLastRowFromTableByProcessDefinitionNameAndBusinessKey(processDefinitionName,
                businessKey, testContext);
        return this;
    }

    public MyServicesPage downloadExcerpt(String processDefinitionName, String businessKey, TestContext testContext) {
        Row serviceRow = new Table().getLastRowFromTableByProcessDefinitionNameAndBusinessKey(processDefinitionName,
                businessKey, testContext);
        WebElement excerptButton = serviceRow.getResult().findElement(By.xpath("div/button"));
        wait.until(elementToBeClickable(excerptButton));
        wait.until(textToBePresentInElement(excerptButton, "Завантажити витяг"));
        excerptButton.click();
        return this;
    }
}
