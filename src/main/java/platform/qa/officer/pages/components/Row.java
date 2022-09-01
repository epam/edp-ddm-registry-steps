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

import static platform.qa.date.DateConverter.convertDateTimeByPattern;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

@AllArgsConstructor
@Getter
@Setter
public class Row implements Comparable<Row> {
    @FindBy(xpath = "td[@id='processDefinitionName']")
    private WebElement processDefinitionName;
    @FindBy(xpath = "td[@id='businessKey']")
    private WebElement businessKey;
    @FindBy(xpath = "td[@id='taskDefinitionName']")
    private WebElement taskDefinitionName;
    @FindBy(xpath = "td[@id='startTime']")
    private WebElement startTime;
    @FindBy(xpath = "td[@id='endTime']")
    private WebElement endTime;
    @FindBy(xpath = "td//button")
    private WebElement actionButton;

    @Override
    public int compareTo(Row otherRow) {
        return convertDateTimeByPattern(getStartTime().getText(), "dd.mm.yyyy hh:mm")
                .compareTo(convertDateTimeByPattern(otherRow.getStartTime().getText(), "dd.mm.yyyy hh:mm"));
    }
}
