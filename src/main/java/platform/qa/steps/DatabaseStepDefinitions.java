package platform.qa.steps;

import static org.assertj.core.api.Assertions.assertThat;

import io.cucumber.java.uk.Дано;
import io.cucumber.java.uk.Тоді;
import lombok.NonNull;
import platform.qa.base.BaseSteps;
import platform.qa.base.FileUtils;
import platform.qa.cucumber.TestContext;
import platform.qa.db.TableInfoDb;
import platform.qa.enums.Context;

public class DatabaseStepDefinitions extends BaseSteps {
    private TestContext testContext;

    public DatabaseStepDefinitions(TestContext testContext) {
        this.testContext = testContext;
    }

    @Дано("розгорнута модель даних з переліком таблиць та згенерованими запитами доступу та пошуку даних")
    public void data_factory_initialize_verification() {
        assertThat(config.getDataFactoryService().getUrl())
                .as("Модель даних не розгорнута!!!")
                .isNotNull();
    }

    @Тоді("виконується запит до бази даних з файлу {string}")
    public void user_execute_query_from_file(@NonNull String selectFileName) {
        String selectQuery = FileUtils.readFromFile("src/test/resources/data/queries/", selectFileName);
        var result = new TableInfoDb(getPrimarySourceDb()).waitAndGetEntity(selectQuery);
        testContext.getScenarioContext().setContext(Context.DB_RESULT_LIST, result);
    }

    @Тоді("виконується запит до бази даних:")
    public void user_execute_query(@NonNull String selectQuery) {
        var result = new TableInfoDb(getPrimarySourceDb()).waitAndGetEntity(selectQuery);
        testContext.getScenarioContext().setContext(Context.DB_RESULT_LIST, result);
    }

}