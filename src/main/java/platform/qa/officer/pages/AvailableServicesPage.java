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

import static org.openqa.selenium.support.ui.ExpectedConditions.textToBePresentInElement;
import static org.openqa.selenium.support.ui.ExpectedConditions.visibilityOf;
import static platform.qa.enums.Section.AVAILABLE_SERVICES;

import lombok.Getter;
import platform.qa.officer.panel.OfficerHeaderPanel;

import java.util.Arrays;
import java.util.stream.Collectors;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

public class AvailableServicesPage extends OfficerBasePage {

    @Getter
    private final OfficerHeaderPanel headerPanel = new OfficerHeaderPanel();

    private final String availableServices = AVAILABLE_SERVICES.getSection();

    @FindBy(xpath = "//div[@data-xpath='header']/following-sibling" + "::div//h1")
    private WebElement availableServicesHeader;

    public AvailableServicesPage() {
        loadingPage();
        loadingComponents();
        checkAvailableServicesHeader();
    }

    WebElement getProcessPath(String processName) {
        String processNameWithDifferentQuotes = getTextPathWithDifferentQuotes(processName);
        return driver.findElement(By.xpath(String.format("//h5[text()=%s]", processNameWithDifferentQuotes)));
    }

    WebElement getProcessGroupPath(String processGroupName) {
        String processGroupNameWithDifferentQuotes = getTextPathWithDifferentQuotes(processGroupName);
        return driver.findElement(By.xpath(String.format("//h3[text()=%s]", processGroupNameWithDifferentQuotes)));
    }

    public AvailableServicesPage checkAvailableServicesHeader() {
        wait.until(textToBePresentInElement(availableServicesHeader, availableServices));
        return this;
    }

    public void clickOnProcessByName(String processName) {
        wait.until(visibilityOf(getProcessPath(processName)))
                .click();
    }

    public AvailableServicesPage clickOnProcessGroupByName(String processGroupName) {
        wait.until(visibilityOf(getProcessGroupPath(processGroupName)))
                .click();
        return this;
    }

    public AvailableServicesPage checkProcessByName(String processName) {
        wait.until(visibilityOf(getProcessPath(processName)));
        return this;
    }

    public AvailableServicesPage checkProcessGroupByName(String processGroupName) {
        wait.until(visibilityOf(getProcessGroupPath(processGroupName)));
        return this;
    }

    private String getTextPathWithDifferentQuotes(String text) {
        String path = String.format("\"%s\"", text);
        if (text.contains("\"")) {
            text =
                    Arrays.stream(text.split("\""))
                            .map(item -> String.format("\"%s\"", item) + ",'\"'")
                            .collect(Collectors.joining(","));
            path = String.format("concat(%s)", text);
        }
        return path;
    }
}
