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

import java.util.LinkedList;
import java.util.List;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.CacheLookup;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;

public class Table extends BasePage {
    @CacheLookup
    @FindBy(xpath = ".//table[@aria-label='table']/tbody//tr")
    List<WebElement> webTableRows;
    List<Row> tableRows = new LinkedList<>();

    public Table() {
        loadingPage();
        loadingComponents();
        wait.until(ExpectedConditions.presenceOfElementLocated(xpath("//table[@aria-label='table']")));
        wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(xpath("//table[@aria-label='table']/tbody//tr")));
        setTableRows();
    }

    public void setTableRows() {
        webTableRows.forEach(row -> {
            Row taskRow = Row.builder()
                    .processDefinitionName(row.findElement(xpath("td[@id='processDefinitionName']")))
                    .businessKey(row.findElement(xpath("td[@id='businessKey']")))
                    .taskDefinitionName(getOptionalElement(row, "td[@id='name' or @id='taskDefinitionName']"))
                    .startTime(row.findElement(xpath("td[@id='created' or @id='startTime']")))
                    .endTime(getOptionalElement(row, "td[@id='endTime']"))
                    .result(getOptionalElement(row, "td[@id='excerptResult' or @id='status.title']"))
                    .actionButton(getOptionalElement(row, "td//button"))
                    .build();
            tableRows.add(taskRow);
        });
    }

    public Row getLastRowFromTableByTaskName(String taskName) {
        return tableRows.stream()
                .filter(row -> row.getTaskDefinitionName().getText().contains(taskName))
                .max(Row::compareTo).orElseThrow(() -> new NoSuchElementException(String.format("???????????? \"%s\" "
                        + "???????????????? ?? ?????????? ??????????", taskName)));
    }

    public Row getLastRowFromTableByProcessDefinitionNameAndBusinessKey(String processDefinitionName,
                                                                        String businessKey) {
        return tableRows.stream()
                .filter(row ->
                        row.getProcessDefinitionName().getText().contains(processDefinitionName) &&
                                row.getBusinessKey().getText().contains(businessKey))
                .max(Row::compareTo).orElseThrow(() -> new NoSuchElementException(String.format("?????????????? \"%s\" ?? "
                        + "?????????????????????????????? \"%s\" ???????????????? ?? ??????????????", processDefinitionName, businessKey)));
    }

    public Row getLastRowFromTableByProcessBusinessKeyAndResult(String processDefinitionName,
                                                                String businessKey, String result) {
        return tableRows.stream()
                .filter(row ->
                        row.getProcessDefinitionName().getText().contains(processDefinitionName) &&
                                row.getBusinessKey().getText().contains(businessKey) &&
                                row.getResult().getText().contains(result))
                .max(Row::compareTo).orElseThrow(() -> new NoSuchElementException(String.format("?????????????? \"%s\" ?? "
                                + "?????????????????????????????? \"%s\" ???? ?????????????????????? \"%s\" ???????????????? ?? ?????????? ????????????",
                        processDefinitionName, businessKey, result)));
    }

    public Row getLastRowByProcessBusinessKeyTaskName(String definitionName, String businessKey, String taskName) {
        return tableRows.stream().filter(row -> row.getProcessDefinitionName().getText().equals(definitionName) &&
                        row.getBusinessKey().getText().equals(businessKey) && row.getTaskDefinitionName().getText().equals(taskName))
                .max(Row::compareTo).orElseThrow(() -> new NoSuchElementException(String.format("?????????? ???????????? ?? "
                                + "???????????????? (%s), ?????????????????????????????? ?????????????? (%s) ?? ?????????????? (%s)", definitionName,
                        businessKey, taskName)));
    }

    private WebElement getOptionalElement(WebElement row, String xpathExpression) {
        try {
            return row.findElement(xpath(xpathExpression));
        } catch (NoSuchElementException ex) {
            return null;
        }
    }
}
