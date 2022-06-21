package platform.qa.officer.pages;

import static org.assertj.core.api.Assertions.assertThat;
import static org.openqa.selenium.support.ui.ExpectedConditions.textToBePresentInElement;
import static org.openqa.selenium.support.ui.ExpectedConditions.visibilityOf;

import platform.qa.officer.pages.components.Table;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedCondition;

public class MyTasksPage extends OfficerBasePage {

    private final String myTasksTextUa = "Мої задачі";

    @FindBy(xpath = "//button[@data-xpath='tabsButton-closed']")
    private WebElement provisionedTasksTab;
    @FindBy(xpath = "//div[@data-xpath='header']/following-sibling::div//h1")
    private WebElement myTasksHeader;
    @FindBy(xpath = "//div[@data-testid='timed-notification']")
    private WebElement timedCreatedNotification;

    public MyTasksPage() {
        loadingPage();
        loadingComponents();
        checkMyTasksHeader();
    }

    public MyTasksPage checkTaskExistsByTaskName(String taskName) {
        wait
                .withMessage(String.format("Задача \"%s\" відсутня у черзі задач", taskName))
                .until((ExpectedCondition<Boolean>) d -> new Table()
                        .getRowFromTableByTaskName(taskName).size() > 0);
        wait = getDefaultWebDriverWait();
        return this;
    }

    public MyTasksPage checkMyTasksHeader() {
        wait.until(textToBePresentInElement(myTasksHeader, myTasksTextUa));
        return this;
    }

    public MyTasksPage clickOnProvisionedTasksTab() {
        wait
                .until(visibilityOf(provisionedTasksTab))
                .click();
        return this;
    }

    public MyTasksPage checkNotificationMessage(String taskName) {
        String message = String.format("Вітаємо!\nЗадача “%s” виконана успішно!", taskName);
        wait
                .until(visibilityOf(timedCreatedNotification));
        assertThat(timedCreatedNotification.getText()).as("Поточне повідомлення не збігається з очікуваним").isEqualTo(message);
        return this;
    }
}
