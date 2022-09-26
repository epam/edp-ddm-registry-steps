package platform.qa.steps.cabinet;

import static java.util.Objects.isNull;
import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static platform.qa.base.convertors.ContextConvertor.convertToFile;
import static platform.qa.base.convertors.ContextConvertor.convertToFileList;
import static platform.qa.enums.Context.DOWNLOAD_FILES;
import static platform.qa.enums.Context.LAST_DOWNLOAD_FILE;

import io.cucumber.java.uk.Тоді;
import lombok.NonNull;
import lombok.extern.log4j.Log4j2;
import platform.qa.base.providers.WebDriverProvider;
import platform.qa.base.utils.CustomFileUtils;
import platform.qa.cucumber.TestContext;
import platform.qa.officer.panel.OfficerHeaderPanel;

import java.io.File;
import java.time.Duration;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;

@Log4j2
public class ExcerptStepDefinitions {
    public static final int WAIT_FILE_LOAD = 30;
    public static final int POOL_INTERVAL = 1;

    private TestContext testContext;


    public ExcerptStepDefinitions(TestContext testContext) {
        this.testContext = testContext;
    }

    @Тоді("завантажує витяг послуги {string} з ідентифікатором {string}")
    public void downloadExcerpt(String processDefinitionName, String businessKey) {
        File downloadDir = new File(WebDriverProvider.chromeDownloadPath);
        List<File> files = (List<File>) FileUtils.listFiles(downloadDir, null, true);
        testContext.getScenarioContext().setContext(DOWNLOAD_FILES, files);
        new OfficerHeaderPanel().clickOnMyServicesLink()
                .clickOnProvidedServicesTab()
                .downloadExcerpt(processDefinitionName, businessKey);
    }

    @Тоді("витяг послуги {string} з ідентифікатором {string} завантажено")
    public void checkExcerptDownloaded(String processDefinitionName, String businessKey) {
        List<File> beforeLoadFiles = convertToFileList(testContext.getScenarioContext().getContext(DOWNLOAD_FILES));
        File downloadDir = new File(WebDriverProvider.chromeDownloadPath);
        await()
                .atMost(Duration.ofSeconds(WAIT_FILE_LOAD))
                .pollInterval(Duration.ofSeconds(POOL_INTERVAL))
                .untilAsserted(() -> assertThat(FileUtils.listFiles(downloadDir, null, true))
                        .as("Файл витягу не з'явився в папці " + downloadDir.getAbsolutePath())
                        .hasSizeGreaterThan(beforeLoadFiles.size()));
        File downloadedExcerptFile = FileUtils.listFiles(downloadDir, null, true).stream()
                .filter(file -> !beforeLoadFiles.contains(file))
                .max(Comparator.comparing(CustomFileUtils::getFileModifiedTime))
                .orElseThrow();
        log.info("Excerpt file to compare: " + downloadedExcerptFile.getAbsolutePath());
        testContext.getScenarioContext().setContext(LAST_DOWNLOAD_FILE, downloadedExcerptFile);
    }

    @Тоді("витяг у форматі csv містить наступні дані:")
    public void checkCSVExcerptContainsData(@NonNull List<Map<String, String>> excerptExpectedData) {
        File excerptFile =
                convertToFile(testContext.getScenarioContext().getContext(LAST_DOWNLOAD_FILE));
        List<Map<String, String>> actualResultList = CustomFileUtils.parseCsvFile(excerptFile);
        List<Map<String, String>> expectedResultList = excerptExpectedData.stream()
                .map(map -> map.entrySet().stream()
                        .collect(Collectors.toMap(Map.Entry::getKey, entry -> isNull(entry.getValue()) ?
                                StringUtils.EMPTY : entry.getValue())))
                .collect(Collectors.toList());
        assertThat(actualResultList).as("Excerpt doesn't contain expected data").containsAll(expectedResultList);
    }

}
