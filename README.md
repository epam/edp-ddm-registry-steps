# registry-steps

### Overview

* The main purpose of the registry-steps library is to provide BDD step definitions to use them in registry BDD tests;
* included all dependencies needed for registry BDD tests.

### Steps list

#### DataModelStepDefinitions

* @Дано("розгорнута модель даних з переліком таблиць та згенерованими запитами доступу та пошуку даних")
* @Тоді("дата модель повертає точно заданий json нижче:$")
* @Тоді("дата модель повертає json з файлу {string}")
* @Тоді("дата модель повертає json, який містить точно наступні дані, ігноруючі невказані:$")
* @Тоді("дата модель повертає точно заданий json з файлу {string}, ігноруючі невказані")

#### RegulationsStepDefinitions

* @Дано("регламент реєстру розгорнуто")
* @Коли("адміністратор регламенту отримує наявні бізнес процеси та відповідні їм форми через сервіси платформи")
* @Тоді("він переконується, що бізнес процеси та їх форми доступні кінцевому користувачу")

#### RestApiStepDefinitions

* @Коли("виконується запит пошуку {string} з параметрами")
* @Коли("виконується запит пошуку {string} без параметрів")
* @Тоді("результат запиту містить наступні значення {string} у полі {string}")

#### OfficerCabinetStepDefinitions

* @Дано("користувач {string} успішно увійшов у кабінет посадової особи")
* @Та("бачить доступний процес {string}")
* @Коли("користувач ініціює процес {string}")
* @Коли("бачить форму {string} із кнопкою "Далі" яка {booleanValue}")
* @Коли("поле {string} має значення заповнене автоматично")
* @Коли("користувач заповнює форму даними$")
* @І("додає запис до {string} таблиці із даними")
* @Та("натискає кнопку "Далі"")
* @Та("на формі {string} бачить повідомлення {string} з текстом:")
* @Коли("пересвідчується в правильному відображенні введених даних на формі {string}")
  @Коли("підписує дані")
* @Тоді("процес закінчено успішно й задача {string} відображається як виконана у переліку задач")

### Test execution

* Tests could be run via maven command:
    * `mvn verify` OR using appropriate functions of your IDE.

### License

The registry-steps is Open Source software released under
the [Apache 2.0 license](https://www.apache.org/licenses/LICENSE-2.0).

