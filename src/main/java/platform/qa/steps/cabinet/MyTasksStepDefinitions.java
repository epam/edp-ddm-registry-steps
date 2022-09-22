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

package platform.qa.steps.cabinet;

import io.cucumber.java.uk.Тоді;
import platform.qa.cucumber.TestContext;
import platform.qa.officer.pages.MyTasksPage;
import platform.qa.officer.panel.OfficerHeaderPanel;

/**
 * Cucumber step definitions for cabinet portal MyTasks page
 */
public class MyTasksStepDefinitions {
    private TestContext testContext;

    public MyTasksStepDefinitions(TestContext testContext) {
        this.testContext = testContext;
    }


    @Тоді("процес закінчено успішно й задача {string} відображається як виконана у переліку задач")
    public void verifyTaskCompleted(String taskName) {
        new MyTasksPage().checkNotificationMessage(taskName)
                .getHeaderPanel().clickOnMyTasksLink()
                .clickOnProvisionedTasksTab()
                .checkTaskExistsByTaskName(taskName);
    }

    @Тоді("задача {string} за послугою {string} з ідентифікатором {string} знаходиться у статусі: Задачі для виконання")
    public void verifyTaskServiceIdentifierInProgress(String taskName, String processDefinitionName,
                                                      String businessKey) {
        new OfficerHeaderPanel().clickOnMyTasksLink()
                .checkTaskExistsByProcessBusinessKeyTaskName(processDefinitionName, businessKey, taskName);
    }

    @Тоді("задача {string} за послугою {string} з ідентифікатором {string} виконана")
    public void verifyTaskServiceIdentifierDone(String taskName, String processDefinitionName, String businessKey) {
        new OfficerHeaderPanel().clickOnMyTasksLink()
                .clickOnProvisionedTasksTab()
                .checkTaskExistsByProcessBusinessKeyTaskName(processDefinitionName, businessKey, taskName);
    }

    @Тоді("приймає задачу {string} за послугою {string} з ідентифікатором {string}")
    public void acceptTask(String taskName, String processDefinitionName, String businessKey) {
        new OfficerHeaderPanel().clickOnMyTasksLink()
                .acceptTask(processDefinitionName, businessKey, taskName);
    }

    @Тоді("виконує задачу {string} за послугою {string} з ідентифікатором {string}")
    public void submitTask(String taskName, String processDefinitionName, String businessKey) {
        new OfficerHeaderPanel().clickOnMyTasksLink()
                .submitTask(processDefinitionName, businessKey, taskName);
    }
}
