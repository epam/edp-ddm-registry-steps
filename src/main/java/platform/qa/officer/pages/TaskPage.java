package platform.qa.officer.pages;

import static java.lang.String.format;
import static java.util.Objects.requireNonNull;
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

import platform.qa.entities.FieldData;
import platform.qa.officer.pages.components.Select;

import org.apache.hc.core5.http.nio.support.TerminalAsyncServerFilter;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedCondition;

public class TaskPage extends CommonTaskPage {

    @FindBy(xpath = "//div[contains(@role, 'dialog')]//button[contains(@type, 'submit')]")
    protected WebElement saveButton;

    private final String inputPath = "//label[text()[contains(.,\"%s\")]]/following-sibling::div//input[@type='text']";
    private final String radioButtonPath = "//span[text()[contains(.,\"%s\")]]/preceding-sibling::span//input[@type='radio']";
    private final String checkboxPath = "//span[text()[contains(.,\"%s\")]]/parent::span/preceding-sibling::span//input[@type='checkbox']";
    private final String dateTimePath = "//label[text()[contains(.,\"%s\")]]/following-sibling::div//input[@type='text']";
    private final String addNewRowButtonPath = "//label[text()[contains(.,\"%s\")]]/following-sibling::div//button";
    private final String addButton = "//label[text()[contains(.,\"%s\")]]/following-sibling::div//button";

    public TaskPage() {
        super();
    }

    public void checkFieldsAreNotEditable() {
        By fieldPath = xpath("//input[contains(@name, 'data')]");
        wait.until(numberOfElementsToBeMoreThan(fieldPath, 0));
        wait.until(attributeContains(fieldPath, "disabled", "true"));
    }

    public void fillInputFieldWithData(String fieldName, String fieldData) {
        wait
                .until(presenceOfElementLocated(xpath(format(this.inputPath, fieldName))))
                .sendKeys(HOME, chord(SHIFT, END), BACK_SPACE, fieldData);
    }


    public void checkSubmitButtonState(boolean isEnabled) {
        if (isEnabled) {
            wait.until(elementToBeClickable(xpath("//button[@type=\"submit\"]")));
        } else {
            wait.until(not(elementToBeClickable(xpath("//button[@type=\"submit\"]"))));
        }

    }

    public void selectDataFromDateTime(String fieldName, String fieldData) {
        WebElement dateTime = driver.findElement(xpath(format(dateTimePath, fieldName)));
        wait
                .until(visibilityOf(dateTime))
                .sendKeys(HOME, chord(SHIFT, END), BACK_SPACE, fieldData, TAB);
    }

    public void checkRadioButton(String fieldData) {
        WebElement radioButton = driver.findElement(xpath(format(radioButtonPath, fieldData)));
        wait
                .until((ExpectedCondition<Boolean>) driver -> radioButton.isEnabled());
        radioButton.click();
    }

    public void checkCheckBox(String fieldName, String fieldData) {
        if (fieldData.equalsIgnoreCase("так")) {
            WebElement checkBox = driver.findElement(xpath(format(checkboxPath, fieldName)));
            wait
                    .until((ExpectedCondition<Boolean>) driver -> checkBox.isEnabled());
            checkBox.click();
        }

    }

    public void checkFieldIsNotEmpty(String fieldName) {
        wait.until((ExpectedCondition<Boolean>) d -> !requireNonNull(d)
                .findElement(xpath(format(inputPath, fieldName)))
                .getAttribute("value")
                .isEmpty());
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
        }
    }

    public void addNewRow(String tableName) {
        wait
                .until(elementToBeClickable(xpath(String.format(addNewRowButtonPath, tableName))))
                .click();
    }

    public void clickAddButton(String gridName) {
        wait
                .until(elementToBeClickable(xpath(String.format(addButton, gridName))))
                .click();
    }

    public TaskPage clickSaveButton (){
        wait
                .until(elementToBeClickable(saveButton))
                .click();
        return this;
    }

}

