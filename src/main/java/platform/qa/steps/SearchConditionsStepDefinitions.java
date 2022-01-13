package platform.qa.steps;

import static org.assertj.core.api.Assertions.assertThat;

import io.cucumber.java.uk.Дано;
import io.cucumber.java.uk.Коли;
import io.cucumber.java.uk.Та;
import io.cucumber.java.uk.Тоді;
import lombok.NonNull;
import platform.qa.base.FileUtils;
import platform.qa.common.RestApiClient;
import platform.qa.cucumber.TestContext;
import platform.qa.db.TableInfoDb;
import platform.qa.enums.Context;
import platform.qa.base.BaseSteps;

import java.util.List;
import java.util.Map;

public class SearchConditionsStepDefinitions extends BaseSteps {
    private final TestContext testContext;

    public SearchConditionsStepDefinitions(TestContext testContext) {
        this.testContext = testContext;
    }

    @Дано("^створений RestApi клієнт$")
    public void getRestApiClient() {

    }

    @Коли("^користувач робить запит \"([^\"]*)\" з параметрами$")
    public void user_execute_Api_with_Path_and_Parameters(@NonNull String path,
                                                          @NonNull Map<String, String> queryParams) {
        var result = new RestApiClient(config.getDataFactoryService())
                .sendGetWithParams(path, queryParams)
                .extract()
                .response()
                .jsonPath()
                .getList("", Map.class);
        testContext.getScenarioContext().setContext(Context.API_RESULT_LIST, result);
    }

    @Тоді("^користувач робить запит до бази даних \"([^\"]*)\" для перевірки$")
    public void user_execute_Query_with_Parameters(@NonNull String selectFileName) {
        String selectQuery = FileUtils.readFromFile("src/test/resources/data/queries/", selectFileName);
        var result = new TableInfoDb(primarySourceDb).waitAndGetEntity(selectQuery);
        testContext.getScenarioContext().setContext(Context.DB_RESULT_LIST, result);
    }

    @Та("^звіряє результат запиту з даними з бази даних$")
    public void compare_Api_and_Select_result() {
        var actualResult = (List<Map>) testContext.getScenarioContext().getContext(Context.API_RESULT_LIST);
        var expectedResult = (List<Map>) testContext.getScenarioContext().getContext(Context.DB_RESULT_LIST);
        assertThat(actualResult).as("Count of rows are different:").hasSameSizeAs(expectedResult);
        assertThat(actualResult).as("Rows are different:").hasSameElementsAs(expectedResult);
    }

}