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

import platform.qa.base.BasePage;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;

import static java.lang.String.format;
import static org.openqa.selenium.By.xpath;

public class Table extends BasePage {

    private final String tableRowsPath = "//tbody//tr[contains(@class, 'MuiTableRow-root')]";
    private final String processDefinitionNamePath = tableRowsPath + "[%d]/td[@id='processDefinitionName']";
    private final String businessKeyPath = tableRowsPath + "[%d]/td[@id='businessKey']";
    private final String taskDefinitionNamePath = tableRowsPath + "[%d]/td[@id='taskDefinitionName']";
    private final String startTimePath = tableRowsPath + "[%d]/td[@id='startTime']";
    private final String endTimePath = tableRowsPath + "[%d]/td[@id='endTime']";

    public Table() {
        loadingPage();
        loadingComponents();
    }

    public List<Row> getRowsFromTableByTaskName(String taskName) {
        return getTableRows().stream()
                .filter(row -> row.getTaskDefinitionName().contains(taskName))
                .collect(Collectors.toList());
    }

    public List<Row> getRowsFromTableByProcessDefinitionNameAndBusinessKey(String processDefinitionName, String businessKey) {
        return getTableRows().stream()
                .filter(row ->
                        row.getProcessDefinitionName().contains(processDefinitionName) &&
                        row.getBusinessKey().contains(businessKey))
                .collect(Collectors.toList());
    }

    private List<Row> getTableRows() {
        List<Row> rowsList = new ArrayList<>();
        List<WebElement> tableRows = driver.findElements(xpath(tableRowsPath));
        if (!tableRows.isEmpty()) {
            wait.withMessage("Елементи в таблиці недоступні").until(ExpectedConditions.visibilityOfAllElements(tableRows));
            wait = getDefaultWebDriverWait();
        }
        for (int i = 0; i < tableRows.size(); i++) {
            rowsList.add(
                    new Row(
                            getProcessDefinitionNameForTheRow(i + 1).getText(),
                            getBusinessKeyForTheRow(i + 1).getText(),
                            getTaskDefinitionNameForTheRow(i + 1).getText(),
                            getStartTimeForTheRow(i + 1).getText(),
                            getEndTimeForTheRow(i + 1).getText()
                    ));
        }
        return rowsList;
    }

    private WebElement getProcessDefinitionNameForTheRow(int rowNumber) {
        return driver.findElement(xpath(format(processDefinitionNamePath, rowNumber)));
    }

    private WebElement getBusinessKeyForTheRow(int rowNumber) {
        return driver.findElement(xpath(format(businessKeyPath, rowNumber)));
    }

    private WebElement getTaskDefinitionNameForTheRow(int rowNumber) {
        return driver.findElement(xpath(format(taskDefinitionNamePath, rowNumber)));
    }

    private WebElement getStartTimeForTheRow(int rowNumber) {
        return driver.findElement(xpath(format(startTimePath, rowNumber)));
    }

    private WebElement getEndTimeForTheRow(int rowNumber) {
        return driver.findElement(xpath(format(endTimePath, rowNumber)));
    }
}
