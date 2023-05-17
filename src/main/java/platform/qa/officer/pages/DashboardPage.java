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

import static org.openqa.selenium.support.ui.ExpectedConditions.visibilityOf;
import static platform.qa.enums.Section.AVAILABLE_SERVICES;
import static platform.qa.enums.Section.MY_SERVICES;
import static platform.qa.enums.Section.MY_TASKS;
import static platform.qa.enums.Section.REPORTS;

import lombok.Getter;
import platform.qa.officer.panel.OfficerHeaderPanel;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

public class DashboardPage extends OfficerBasePage {

    @Getter
    private final OfficerHeaderPanel headerPanel = new OfficerHeaderPanel();

    private final String availableServices = AVAILABLE_SERVICES.getSection();
    private final String myServices = MY_SERVICES.getSection();
    private final String myTasks = MY_TASKS.getSection();
    private final String reports = REPORTS.getSection();

    public DashboardPage() {
        loadingPage();
        loadingComponents();
        checkServices();
    }

    public WebElement availableServices(String text) {
        return driver.findElement(By.xpath(String.format("//div[@data-xpath='processDefinitionMenuOption']//a"
                + "[contains(text(), '%s')]", text)));
    }

    public WebElement myServices(String text) {
        return driver.findElement(By.xpath(String.format("//div[@data-xpath='processActiveMenuOption']//a[contains"
                + "(text(), '%s')]", text)));
    }

    public WebElement myTasks(String text) {
        return driver.findElement(By.xpath(String.format("//div[@data-xpath='taskMenuOption']//a[contains(text(), "
                + "'%s')]", text)));
    }

    public WebElement reports(String text) {
        return driver.findElement(By.xpath(String.format("//div[@data-xpath='reportMenuOption']//a[contains(text(), "
                + "'%s')]", text)));
    }

    public DashboardPage checkServices() {
        wait.until(visibilityOf(availableServices(availableServices)));
        wait.until(visibilityOf(myServices(myServices)));
        wait.until(visibilityOf(myTasks(myTasks)));
        wait.until(visibilityOf(reports(reports)));
        return this;
    }

    public AvailableServicesPage clickOnAvailableServices() {
        wait.until(visibilityOf(availableServices(availableServices)))
                .click();
        return new AvailableServicesPage();
    }

    public MyServicesPage clickOnMyServices() {
        wait.until(visibilityOf(myServices(myServices)))
                .click();
        return new MyServicesPage();
    }

    public MyTasksPage clickOnMyTasks() {
        wait.until(visibilityOf(myTasks(myTasks)))
                .click();
        return new MyTasksPage();
    }
}
