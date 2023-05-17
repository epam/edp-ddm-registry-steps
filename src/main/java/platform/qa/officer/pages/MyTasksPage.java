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

import static org.assertj.core.api.Assertions.assertThat;
import static org.openqa.selenium.support.ui.ExpectedConditions.elementToBeClickable;
import static org.openqa.selenium.support.ui.ExpectedConditions.textToBePresentInElement;
import static org.openqa.selenium.support.ui.ExpectedConditions.visibilityOf;

import lombok.Getter;
import platform.qa.officer.pages.components.Row;
import platform.qa.officer.pages.components.Table;
import platform.qa.officer.panel.OfficerHeaderPanel;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

public class MyTasksPage extends OfficerBasePage {

    @Getter
    private final OfficerHeaderPanel headerPanel = new OfficerHeaderPanel();

    private final String myTasksTextUa = "Мої задачі";

    @FindBy(xpath = "//button[@data-xpath='tabsButton-closed']")
    private WebElement provisionedTasksTab;
    @FindBy(xpath = "//div[@data-xpath='header']/following-sibling::div//h1")
    private WebElement myTasksHeader;
    @FindBy(xpath = "//div[@data-testid='timed-notification']//*[@role='img']/following-sibling::div")
    private WebElement timedCreatedNotification;

    public MyTasksPage() {
        loadingPage();
        loadingComponents();
        checkMyTasksHeader();
    }

    public MyTasksPage acceptTask(String definitionName, String businessKey, String taskName) {
        Row row = new Table().getLastRowByProcessBusinessKeyTaskName(definitionName, businessKey, taskName);
        WebElement actionButton = row.getActionButton();
        wait.until(textToBePresentInElement(actionButton, "Прийняти"));
        wait.until(elementToBeClickable(actionButton)).click();
        return new MyTasksPage();
    }

    public TaskPage submitTask(String definitionName, String businessKey, String taskName) {
        Row row = new Table().getLastRowByProcessBusinessKeyTaskName(definitionName,
                businessKey, taskName);
        WebElement actionButton = row.getActionButton();
        wait.until(textToBePresentInElement(actionButton, "Виконати"));
        wait.until(elementToBeClickable(actionButton)).click();
        return new TaskPage();
    }

    public MyTasksPage checkTaskExistsByProcessBusinessKeyTaskName(String definitionName, String businessKey,
                                                                   String taskName) {
        Row row = new Table().getLastRowByProcessBusinessKeyTaskName(definitionName, businessKey, taskName);
        assertThat(row).as(String.format("Немає запису з Послугою (%s), ідентифікатором послуги (%s) і задачею (%s)",
                definitionName, businessKey, taskName)).isNotNull();
        return this;
    }

    public MyTasksPage checkTaskExistsByTaskName(String taskName) {
        new Table().getLastRowFromTableByTaskName(taskName);
        return this;
    }

    public MyTasksPage checkTaskExistsByProcessDefinitionNameAndBusinessKey(String processDefinitionName,
                                                                            String businessKey) {
        new Table().getLastRowFromTableByProcessDefinitionNameAndBusinessKey(processDefinitionName, businessKey);
        return this;
    }

    public MyTasksPage checkMyTasksHeader() {
        wait.until(textToBePresentInElement(myTasksHeader, myTasksTextUa));
        return this;
    }

    public MyTasksPage clickOnProvisionedTasksTab() {
        wait.until(visibilityOf(provisionedTasksTab))
                .click();
        return new MyTasksPage();
    }

    public MyTasksPage checkNotificationMessage(String taskName) {
        String message = String.format("Вітаємо!\nЗадача “%s” виконана успішно!", taskName);
        wait.until(visibilityOf(timedCreatedNotification));
        assertThat(timedCreatedNotification.getText()).as("Поточне повідомлення не збігається з очікуваним").isEqualTo(message);
        return this;
    }
}
