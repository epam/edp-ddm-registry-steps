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

import lombok.extern.log4j.Log4j2;
import org.apache.commons.io.FilenameUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedCondition;
import platform.qa.cucumber.TestContext;
import platform.qa.entities.FieldData;
import platform.qa.entities.context.Request;
import platform.qa.officer.pages.components.Select;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static java.lang.String.format;
import static java.util.Objects.requireNonNull;
import static org.apache.commons.lang3.StringUtils.substringBetween;
import static org.openqa.selenium.By.xpath;
import static org.openqa.selenium.Keys.BACK_SPACE;
import static org.openqa.selenium.Keys.END;
import static org.openqa.selenium.Keys.HOME;
import static org.openqa.selenium.Keys.SHIFT;
import static org.openqa.selenium.Keys.TAB;
import static org.openqa.selenium.Keys.chord;
import static org.openqa.selenium.support.ui.ExpectedConditions.attributeContains;
import static org.openqa.selenium.support.ui.ExpectedConditions.elementToBeClickable;
import static org.openqa.selenium.support.ui.ExpectedConditions.not;
import static org.openqa.selenium.support.ui.ExpectedConditions.numberOfElementsToBeMoreThan;
import static org.openqa.selenium.support.ui.ExpectedConditions.presenceOfElementLocated;
import static org.openqa.selenium.support.ui.ExpectedConditions.visibilityOf;
import static org.openqa.selenium.support.ui.ExpectedConditions.visibilityOfElementLocated;
import static platform.qa.base.convertors.ContextConvertor.convertToRandomMapContext;
import static platform.qa.base.convertors.ContextConvertor.convertToRequestsContext;
import static platform.qa.enums.Context.API_RESULTS;
import static platform.qa.enums.Context.RANDOM_VALUE_MAP;

@Log4j2
public class TaskPage extends CommonTaskPage {

    @FindBy(xpath = "//button[contains(@ref, 'saveRow')] | //div[contains(@role, 'dialog')]"
            + "//button[contains(@type, 'submit')]")
    protected WebElement saveRowEditGridButton;

    @FindBy(xpath = "//a[contains(@class, 'draw-marker')]")
    protected WebElement drawMakerButton;

    private final String inputPath = "//label[text()[contains(.,\"%s\")]]" +
            "/following-sibling::div//input[@type='text']";
    private final String radioButtonInputPath = "//span[text()[contains(.,\"%s\")]]" +
            "/preceding-sibling::span//input[@type='radio']";
    private final String radioButtonPath = "//span[text()[contains(.,\"%s\")]]" +
            "/preceding-sibling::span";
    private final String checkboxInputPath = "//span[text()[contains(.,\"%s\")]]" +
            "/parent::span/preceding-sibling::span//input[@type='checkbox']";
    private final String checkboxPath = "//span[text()[contains(.,\"%s\")]]" +
            "/parent::span/preceding-sibling::span";
    private final String dateTimePath = "//label[text()[contains(.,\"%s\")]]" +
            "/following-sibling::div//input[@type='text']";
    private final String addNewRowButtonPath = "//label[text()[contains(.,\"%s\")]]" +
            "/following-sibling::div//button";
    private final String textAreaPath = "//label[text()[contains(.,\"%s\")]]" +
            "/following-sibling::div//textarea";
    private final String contentTextPath = "//div[contains(@class,'formio-component-content')]";
    private final String addRowEditGridButtonPath =
            "//label[text()[contains(.,\"%1$s\")]]/parent::div//button |//label[text()[contains(.,\"%1$s\")]]/following-sibling::"
                    + "button | //label[text()[contains(.,\"%1$s\")]]/following-sibling::div/div[contains(@data-xpath, 'Grid]')]/div/following-sibling::button";
    private final String uploadButtonPath = "//label[contains(text(), \"%1$s\")]/following-sibling::div[@ref='fileDrop']/a";
    private final String inputFile = "//input[@type='file']";
    private final String progressBar = "//div[contains(@class,'progress-bar')]";
    private static final String PATH_TO_FILE = "src/test/resources/files/";

    public TaskPage() {
        super();
    }

    public void checkFieldsAreNotEditable() {
        By fieldPath = xpath("//input[contains(@name, 'data')]");
        wait.until(numberOfElementsToBeMoreThan(fieldPath, 0));
        wait.until(attributeContains(fieldPath, "disabled", "true"));
    }

    public void fillInputFieldWithData(String fieldName, String fieldData) {
        By inputField = xpath(format(this.inputPath, fieldName));
        wait.until(presenceOfElementLocated(inputField));
        wait.until((ExpectedCondition<Boolean>) driver ->
                requireNonNull(driver)
                        .findElement(inputField)
                        .isEnabled());
        driver.findElement(inputField).sendKeys(HOME, chord(SHIFT, END), BACK_SPACE, fieldData);
    }

    public void fillTextareaFieldWithData(String fieldName, String fieldData) {
        By textAreaField = xpath(format(this.textAreaPath, fieldName));
        wait.until(presenceOfElementLocated(textAreaField));
        wait.until((ExpectedCondition<Boolean>) driver ->
                requireNonNull(driver)
                        .findElement(textAreaField)
                        .isEnabled());
        driver.findElement(textAreaField).sendKeys(HOME, chord(SHIFT, END), BACK_SPACE, fieldData);
    }

    public void checkFieldIsFilledWithData(String fieldXpath, String fieldData) {
        wait.until(presenceOfElementLocated(xpath(fieldXpath)));
        wait.until(attributeContains(xpath(fieldXpath), "value", fieldData));
    }

    public void checkSubmitButtonState(String buttonName, boolean isEnabled) {
        wait.until(presenceOfElementLocated(xpath(format(buttonXpath, buttonName))));
        if (isEnabled) {
            wait.until(elementToBeClickable(xpath(format(buttonXpath, buttonName))));
        } else {
            wait.until(not(elementToBeClickable(xpath(format(buttonXpath, buttonName)))));
        }
        log.info("Кнопка далі активна = " + driver.findElement(xpath(format(buttonXpath, buttonName))).isEnabled());
    }

    public void selectDataFromDateTime(String fieldName, String fieldData) {
        By dateTime = xpath(format(dateTimePath, fieldName));
        wait.until(presenceOfElementLocated(dateTime));
        wait.until((ExpectedCondition<Boolean>) driver ->
                requireNonNull(driver)
                        .findElement(dateTime)
                        .isEnabled());
        driver.findElement(dateTime).sendKeys(HOME, chord(SHIFT, END), BACK_SPACE, fieldData, TAB);
        wait.until(visibilityOfElementLocated(taskNameBy)).click();
    }

    public void checkRadioButton(String fieldData) {
        By radioButton = xpath(format(radioButtonInputPath, fieldData));
        wait.until(presenceOfElementLocated(radioButton));
        wait.until((ExpectedCondition<Boolean>) driver ->
                requireNonNull(driver)
                        .findElement(radioButton)
                        .isEnabled());
        driver.findElement(radioButton).click();
    }

    public void checkRadioButtonIsChecked(String fieldData) {
        By radioButton = xpath(format(radioButtonInputPath, fieldData));
        wait.until(presenceOfElementLocated(radioButton));
        wait.until((ExpectedCondition<Boolean>) driver -> requireNonNull(driver).findElement(radioButton).getAttribute("checked") != null);
    }

    public void checkCheckBox(String fieldName, String fieldData) {
        By checkBox = xpath(format(checkboxInputPath, fieldName));
        wait.until(presenceOfElementLocated(checkBox));
        if (fieldData.equalsIgnoreCase("так")) {
            wait.until((ExpectedCondition<Boolean>) driver ->
                    requireNonNull(driver)
                            .findElement(checkBox)
                            .isEnabled());
            driver.findElement(checkBox).click();
        }
    }

    public void checkCheckBoxIsChecked(String fieldName, String fieldData) {
        By checkBox = xpath(format(checkboxPath, fieldName));
        wait.until(presenceOfElementLocated(checkBox));
        if (fieldData.equalsIgnoreCase("так")) {
            wait.until(attributeContains(checkBox, "class", "Mui-checked"));
        }
    }

    public void checkFieldIsNotEmpty(String fieldName) {
        By inputField = xpath(format(inputPath, fieldName));
        wait.until(presenceOfElementLocated(inputField));
        wait.until((ExpectedCondition<Boolean>) d -> !requireNonNull(d)
                .findElement(inputField)
                .getAttribute("value")
                .isEmpty());
    }

    public void checkTextareaText(String fieldName, String fieldData) {
        By textarea = xpath(format(textAreaPath, fieldName));
        checkTextInsideBlock(textarea, fieldData);
    }

    public void checkContentText(String fieldData) {
        By content = xpath(contentTextPath);
        checkTextInsideBlock(content, fieldData);
    }

    public void userFillFormFieldsWithData(List<FieldData> rows, TestContext testContext) {
        for (FieldData fieldData : rows) {
            processValue(testContext, fieldData);
            new TaskPage().setFieldsData(fieldData);
        }
    }

    public void setFieldsData(FieldData fieldData) {
        switch (fieldData.getType()) {
            case RADIOBUTTON:
                checkRadioButton(fieldData.getValue());
                break;
            case CHECKBOX:
                checkCheckBox(fieldData.getName(), fieldData.getValue());
                break;
            case INPUT:
                fillInputFieldWithData(fieldData.getName(), fieldData.getValue());
                break;
            case SELECT:
                new Select().selectItemFromDropDown(fieldData.getName(), fieldData.getValue());
                break;
            case DATETIME:
                selectDataFromDateTime(fieldData.getName(), fieldData.getValue());
                break;
            case TEXTAREA:
                fillTextareaFieldWithData(fieldData.getName(), fieldData.getValue());
                break;
        }
    }

    public void checkFieldsData(FieldData fieldData, TestContext testContext) {
        processValue(testContext, fieldData);
        switch (fieldData.getType()) {
            case RADIOBUTTON:
                checkRadioButtonIsChecked(fieldData.getValue());
                break;
            case CHECKBOX:
                checkCheckBoxIsChecked(fieldData.getName(), fieldData.getValue());
                break;
            case INPUT:
            case SELECT:
                checkFieldIsFilledWithData(format(this.inputPath, fieldData.getName()), fieldData.getValue());
                break;
            case DATETIME:
                checkFieldIsFilledWithData(format(dateTimePath, fieldData.getName()), fieldData.getValue());
                break;
            case TEXTAREA:
                checkTextareaText(fieldData.getName(), fieldData.getValue());
                break;
        }
    }

    public void addNewRow(String tableName) {
        wait.until(elementToBeClickable(xpath(format(addNewRowButtonPath, tableName))))
                .click();
    }

    public void clickAddRowEditGridButton(String gridName) {
        wait.until(elementToBeClickable(xpath(format(addRowEditGridButtonPath, gridName))))
                .click();
    }

    public TaskPage clickSaveRowEditGridButton() {
        wait.until(elementToBeClickable(saveRowEditGridButton))
                .click();
        return this;
    }

    private void checkTextInsideBlock(By textarea, String fieldData) {
        wait.until(presenceOfElementLocated(textarea));
        String actualText = getTrimTextWithoutEmptyLines(driver.findElement(textarea).getText());
        String expectedText = getTrimTextWithoutEmptyLines(fieldData);
        wait.until(
                new ExpectedCondition<Boolean>() {
                    public Boolean apply(WebDriver driver) {
                        return actualText.equalsIgnoreCase(expectedText);
                    }

                    public String toString() {
                        return String.format("Expected text ('%s') but was ('%s')", actualText, expectedText);
                    }
                });
    }

    private String getTrimTextWithoutEmptyLines(String inputText) {
        var text = inputText.replaceAll("(?m)^[ \t]*\r?\n", "").trim();
        return Arrays.stream(text.split("\n"))
                .map(String::trim)
                .collect(Collectors.joining("\n"));
    }

    public void setPoint(int x, int y) {
        wait.until(visibilityOf(drawMakerButton)).click();
        new Actions(driver).moveToElement(drawMakerButton, x, y).click().build().perform();
    }

    public void uploadFile(String fileName, String fieldName) {
        disableClickEventOnInputFile();
        wait.until(elementToBeClickable(xpath(format(uploadButtonPath, fieldName)))).click();
        File file = new File(PATH_TO_FILE, FilenameUtils.getName(fileName));
        if (file.exists()) {
            driver.findElement(xpath(inputFile)).sendKeys(file.getAbsolutePath());
            wait.until(not(visibilityOfElementLocated(xpath(progressBar))));
        }
    }

    public void disableClickEventOnInputFile() {
        ((JavascriptExecutor) driver)
                .executeScript("HTMLInputElement.prototype.click = function() { if(this.type !== 'file') HTMLElement"
                        + ".prototype.click.call(this);};");
    }

    private void processValue(TestContext testContext, FieldData fieldData) {
        var context = convertToRequestsContext(testContext.getScenarioContext().getContext(API_RESULTS));
        var randomValueMap = convertToRandomMapContext(testContext.getScenarioContext().getContext(RANDOM_VALUE_MAP));
        String valueKey = substringBetween(fieldData.getValue(), "{", "}");
        if (fieldData.getValue().startsWith("{")
                && fieldData.getValue().endsWith("}")
                && randomValueMap.containsKey(valueKey)) {
            fieldData.setValue(randomValueMap.get(valueKey));
        } else if (fieldData.getValue().startsWith("{")
                && fieldData.getValue().endsWith("}")
                && context.stream().anyMatch(request -> request.isResultContainsKey(valueKey))) {
            var lastRequest = context.stream()
                    .filter(request -> request.isResultContainsKey(valueKey))
                    .max(Request::compareTo);
            lastRequest.ifPresent(request -> fieldData.setValue(request.getResultValueByKey(valueKey)));
        }
    }
}
