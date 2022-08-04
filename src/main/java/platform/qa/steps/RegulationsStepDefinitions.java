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

package platform.qa.steps;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.SoftAssertions.assertSoftly;

import io.cucumber.java.uk.Дано;
import io.cucumber.java.uk.Коли;
import io.cucumber.java.uk.Тоді;
import lombok.extern.log4j.Log4j2;
import platform.qa.api.FormManagementProviderApi;
import platform.qa.api.ProcessDefinitionApi;
import platform.qa.base.convertors.Convertor;
import platform.qa.configuration.MasterConfig;
import platform.qa.configuration.RegistryConfig;
import platform.qa.cucumber.TestContext;
import platform.qa.entities.BusinessProcess;
import platform.qa.entities.Definition;
import platform.qa.entities.Repository;
import platform.qa.enums.Context;
import platform.qa.git.JgitClient;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Cucumber step definitions for registry regulation
 */
@Log4j2
public class RegulationsStepDefinitions {

    private RegistryConfig registryConfig = MasterConfig.getInstance().getRegistryConfig();
    private Repository gerritRepo = new Repository(registryConfig.getGerrit(), "registry-regulations", "master");
    private TestContext testContext;

    public RegulationsStepDefinitions(TestContext testContext) {
        this.testContext = testContext;
    }

    @Дано("регламент реєстру розгорнуто")
    public void verifyRegistryRegulationsLoad() {
        List<File> bpmnFiles;
        Object bpmn = testContext.getScenarioContext().getContext(Context.BPMN_FILE_NAMES);
        if (!Objects.isNull(bpmn)) {
            bpmnFiles = (List<File>) bpmn;
        } else {
            bpmnFiles = getBpmnFilesFromGerritFolder("bpmn", ".bpmn");
            testContext.getScenarioContext().setContext(Context.BPMN_FILE_NAMES, bpmnFiles);
        }
        assertThat(bpmnFiles).as("Регламент не розгорнувся:").hasSizeGreaterThan(0);
    }

    @Коли("адміністратор регламенту {string} отримує наявні бізнес процеси та відповідні їм форми через сервіси платформи")
    public void getProcessesAndForms(String userName) {
        List<File> bpmnFiles = (List<File>) testContext.getScenarioContext().getContext(Context.BPMN_FILE_NAMES);
        assertThat(bpmnFiles).as("В розгорнутому регламенті немає списку bpmn файлів процесів:").hasSizeGreaterThan(0);

        List<String> formKeys = getFormKeysFromBpmnFiles(bpmnFiles);
        testContext.getScenarioContext().setContext(Context.BPMN_FORM_KEY_LIST, formKeys);

        List<String> processNames = getProcessNamesFromBpmnFiles(bpmnFiles);
        testContext.getScenarioContext().setContext(Context.BPMN_PROCESS_NAME_LIST, processNames);

        List<String> deployedFormKeys = getDeployedFormsFromProvider(userName);
        testContext.getScenarioContext().setContext(Context.API_FORM_KEY_LIST, deployedFormKeys);

        List<String> deployedProcessNames = getDeployedProcessesFromBpms(userName);
        testContext.getScenarioContext().setContext(Context.API_PROCESS_NAME_LIST, deployedProcessNames);
    }

    private List<String> getDeployedProcessesFromBpms(String userName) {
        log.info("Start getting processes from Business Process Management!");
        List<Definition> deployedProcesses =
                new ProcessDefinitionApi(registryConfig.getBpms(userName)).getAllDefinitions();
        return deployedProcesses.stream().map(Definition::getName).collect(Collectors.toList());
    }

    private List<String> getDeployedFormsFromProvider(String userName) {
        log.info("Start getting forms from Form Provider!");
        List<Map> deployedForms =
                new FormManagementProviderApi(registryConfig.getFormManagementProvider(userName)).getAllForms();
        return deployedForms.stream().map(form -> form.get("name").toString()).collect(Collectors.toList());
    }

    @Тоді("він переконується, що бізнес процеси та їх форми доступні кінцевому користувачу")
    public void checkIfAllBpmnFormsWereDeployed() {
        List<String> expectedForms =
                (List<String>) testContext.getScenarioContext().getContext(Context.BPMN_FORM_KEY_LIST);
        List<String> actualForms =
                (List<String>) testContext.getScenarioContext().getContext(Context.API_FORM_KEY_LIST);

        List<String> expectedProcesses =
                (List<String>) testContext.getScenarioContext().getContext(Context.BPMN_PROCESS_NAME_LIST);
        List<String> actualProcesses =
                (List<String>) testContext.getScenarioContext().getContext(Context.API_PROCESS_NAME_LIST);

        assertSoftly(softly -> {
            softly.assertThat(actualForms).as("Кількість розгорнутих форм на оточенні менша ніж в "
                    + "registry-regulations репозиторії:").hasSizeGreaterThanOrEqualTo(expectedForms.size());
            softly.assertThat(actualForms).as("Форми розгорнені на оточенні не співпадають з усіма необхідними в "
                    + "registry-regulations репозиторії").containsAll(expectedForms);

            softly.assertThat(actualProcesses).as("Кількість розгорнутих процесів на оточенні менша ніж в "
                    + "registry-regulations репозиторії:").hasSizeGreaterThanOrEqualTo(expectedProcesses.size());
            softly.assertThat(actualProcesses).as("Процеси розгорнені на оточенні не співпадають з усіма необхідними "
                    + "в registry-regulations репозиторії").containsAll(expectedProcesses);
        });
    }

    private List<String> getFormKeysFromBpmnFiles(List<File> bpmnFiles) {
        List<BusinessProcess> businessProcesses = bpmnFiles.stream()
                .map(file -> Convertor.convertPartOfXmlFileToObject(file, "process", BusinessProcess[].class))
                .flatMap(Arrays::stream)
                .collect(Collectors.toList());
        assertThat(businessProcesses).as("Файлів процесів немає в папці target:").hasSizeGreaterThan(0);

        return businessProcesses.stream()
                .flatMap(bp -> Optional.ofNullable(bp.getUserTask()).stream()
                        .map(userTasks -> userTasks.stream()
                                .map(BusinessProcess.UserTask::getFormKey)
                                .distinct()
                                .collect(Collectors.toList()))
                ).findFirst().orElseThrow();
    }

    private List<String> getProcessNamesFromBpmnFiles(List<File> bpmnFiles) {
        List<String> processNames = bpmnFiles.stream()
                .map(file -> Convertor.convertPartOfXmlFileToObject(file, "process", BusinessProcess[].class))
                .flatMap(Arrays::stream)
                .map(businessProcess -> businessProcess.getName())
                .collect(Collectors.toList());
        assertThat(processNames).as("Файлів процесів немає в папці target:").hasSizeGreaterThan(0);

        return processNames;
    }

    private List<File> getBpmnFilesFromGerritFolder(String folder, String fileExtension) {
        List<List<File>> filesFromFolder = new JgitClient(gerritRepo).getFilesFromFolder(folder);
        assertThat(filesFromFolder).isNotNull();
        return filesFromFolder.get(0).stream()
                .filter(file -> file.getName().endsWith(fileExtension))
                .collect(Collectors.toList());
    }
}
