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

import static org.openqa.selenium.By.xpath;

import platform.qa.base.BasePage;

import java.util.List;
import java.util.stream.Collectors;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;

public class Table extends BasePage {
    @FindBy(xpath = ".//table[@aria-label='table']/tbody//tr")
    List<Row> tableRows;

    private final String tableXpath = "//table[@aria-label='table']";

    public Table() {
        loadingPage();
        loadingComponents();
        wait.until(ExpectedConditions.presenceOfElementLocated(xpath(tableXpath)));
        wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(xpath(tableXpath)));
    }

    public List<Row> getRowsFromTableByTaskName(String taskName) {
        return tableRows.stream()
                .filter(row -> row.getTaskDefinitionName().getText().contains(taskName))
                .collect(Collectors.toList());
    }

    public List<Row> getRowsFromTableByProcessDefinitionNameAndBusinessKey(String processDefinitionName,
                                                                           String businessKey) {
        return tableRows.stream()
                .filter(row ->
                        row.getProcessDefinitionName().getText().contains(processDefinitionName) &&
                                row.getBusinessKey().getText().contains(businessKey))
                .collect(Collectors.toList());
    }

    public Row getRowByProcessBusinessKeyTaskName(String definitionName, String businessKey, String taskName) {
        return tableRows.stream().filter(row -> row.getProcessDefinitionName().getText().equals(definitionName) &&
                        row.getBusinessKey().getText().equals(businessKey) && row.getTaskDefinitionName().getText().equals(taskName))
                .max(Row::compareTo).orElseThrow(() -> new NoSuchElementException(String.format("Немає запису з Послугою (%s)"
                        + ", ідентифікатором послуги (%s) і задачею (%s)", definitionName, businessKey, taskName)));
    }
}
