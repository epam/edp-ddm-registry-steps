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

import java.util.List;
import java.util.stream.Collectors;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;

public class Table extends BasePage {

    @FindBy(xpath = "//tbody//tr[contains(@class, 'MuiTableRow-root')]")
    private List<WebElement> tableRows;

    public Table() {
        loadingPage();
        loadingComponents();
    }

    public List<WebElement> getRowFromTableByTaskName(String taskName) {
        return getTableRows().stream().filter(row->row.getText().contains(taskName)).collect(Collectors.toList());
    }

    private List<WebElement> getTableRows() {
        if (tableRows.isEmpty()) {
            return tableRows;
        } else {
            List<WebElement> tableRows =
                    wait.withMessage("Елементи в таблиці недоступні").until(ExpectedConditions.visibilityOfAllElements(this.tableRows));
            wait = getDefaultWebDriverWait();
            return tableRows;
        }
    }
}
