# Бібліотека з кроками, які можна використовувати в тестах у форматі Gherkin (BDD - Behavior-driven development)

### Загальний опис

* Основною метою бібліотеки кроків є реалізація кроків BDD для використання їх у BDD тестах для реєстру;
* Включено всі залежності, необхідні для запуску тестів.

### Налаштування та запуск тестів

* Тести у форматі Gherkin зберігаються у файлах з розширенням .feature
* Приклад тесту у форматі Gherkin

```gherkin
# language: uk
Функціонал: [StoryNumber] Назва функціоналу що тестується

  Передумова:
    # Так можуть бути відмічені коментарі
	Нехай користувач "auto-user" виконує запит пошуку "users" з параметрами
	  | userName | Ім'я |
	І виконується фільтрація результатів запиту "users" за параметрами
	  | fullName | Повне Ім'я |
	Та користувач "auto-user" виконує запит "users" видалення даних створених в сценарії з назвою параметру "userId"

  Сценарій: [POSITIVE] Перевірка процесу 'Назва процесу'
	Дано користувач "auto-user" успішно увійшов у кабінет посадової особи
	Та бачить доступний процес "Повна назва процесу"
	Коли користувач ініціює процес "Повна назва процесу"
	Та бачить форму "Перша форма" із кнопкою "Далі" яка не активна
	І користувач заповнює форму даними
	  | name            | type        | value      |
	  | Повне ім'я      | input       | Ім'я       |
	  | Стать           | select      | жіноча     |
	  | Дата Народження | datetime    | 2000-02-02 |
	  | Заклад          | radiobutton | Заклад     |
	  | Вища освіта     | checkbox    | Так        |
	Та натискає кнопку "Далі"
	А також пересвідчується в правильному відображенні введених даних на формі "Підписати дані про форму"
	Та підписує дані
	Тоді процес закінчено успішно й задача "Підписати дані форми за допомогою КЕП" відображається як виконана у переліку задач
```

* Після встановлення empty-template при створенні репозиторію реєстра registry-regulations створюється така структура
  для автоматизованих тестів:
    * autotests
        * features - покласти файли тестів з розширенням .feature, також тут можуть бути файли с даними .json і існувати
          різні підпапки в залежності від напрямку тестів (business-process, data, search-conditions)
        * files
            * keys - сюди треба покласти файли ключів для тестових користувачів
        * properties
            * platform.json (empty template) - необхідно заповнити згідно даних openshift на конкретному кластері
            * users.json (empty template) - необхідно заповнити згідно шаблону.
* Тести запускаються в пайплайні MASTER-Build-registry-regulations в блоці run-autotests

### Список реалізованих кроків

* Кроки для написання тестів можна використовувати із будь-якими анотаціями фреймворку Cucumber у тій послідовності, яка
  необхідна
* Кожен файл із тестом повинен починатись із вказання мови, яку використовують для написання тесту `# language: uk` та
  зі слова `Функціонал` після якого вказується назва функціональності, яка перевіряється
* Далі вказується ключове слово `Сценарій`, в якому описано сценарій що перевіряється
* Після сценарію використовуються кроки тесту з анотаціями, які наведені нижче
* Приклади тестів наведено після опису кроків доступних для використання у сценаріях тестування

#### На початку тесту використовуються кроки із наступними анотаціями, які описують передумову початку тесту `@Дано`,`@Припустимо`, `@Припустимо, що`, `@Нехай`

* Якщо передумов декілька, використовують анотації `@І`, `@А також`, `@Та`

#### Далі у тесті використовують анотації `@Коли`, `@Якщо`, які описують необхідні дії для виконання тесту

* Якщо крок повинен виконувати декілька дій, то використовують анотації `@І`, `@А також`, `@Та`

#### В кінці тесту використовують крок, що описує очікуваний результат тесту з анотаціями `@То`, `@Тоді`

* Якщо очікуваних результатів декілька, тоді використовують анотації `@І`, `@А також`, `@Та`

### Нижче наведено кроки, які імплементовані у бібліотеці `registry-steps`

#### Кроки, які використовуються для перевірки розгорнутої моделі даних та згенерованих запитів для пошуку даних

* @Дано("користувачу {string} доступна розгорнута модель даних з переліком таблиць та згенерованими запитами доступу та
  пошуку даних")
    * Для змінної `string` необхідно вказати ім'я користувача кабінету посадової особи, яке буде вказано у `user.json`
      файлі
    * `user.json` файл необхідно додати до репозиторію `registry-tests` за наступним
      шляхом `\src\test\resources\properties\users.json`
    * дані атрибутів заповнюються згідно даних ключа, який необхідно додати до репозиторію `registry-tests` за наступним
      шляхом `\src\test\resources\files\keys\<Key-file-name>.dat` drfo, edrpou, fullName, name, password (конвертується
      у base64), provider  
      Приклад `user.json` файлу наведено нижче

```
    {
      "auto-user": {
      "login": "",
      "realm": "-{realmNameSuffix}",
      "clientId": "",
      "realmRoles": [
        "officer"
        ],
      "attributes": {
      "drfo": [
        ""
        ],
      "edrpou": [
        ""
        ],
      "fullName": [
        ""
        ]
      },
      "key": {
        "name": "",
        "password": "",
        "provider": ""
        }
    }
```

* @Коли("користувач {string} виконує запит пошуку {string} з параметрами")
    * Для першої змінної `string` необхідно вказати ім'я користувача кабінету посадової особи, яке буде вказано
      у `user.json` файлі
    * Для другої змінної `string` необхідно вказати endpoint на який буде відправлено запит методом GET
    * Нижче цього кроку необхідно у вигляді таблиці вказати назву параметра та його значення
    * Якщо значення - це ідентфікатор, отриманий в результаті попередніх запитів, тоді значення має бути записано в
      фігурних дужках
      `| параметр | значення |`  
      Приклад:

```gherkin
     Коли користувач "auto-user" виконує запит пошуку "year-name-contains" з параметрами
       | name | 2022   |
       |userId|{userId}|
```

* @Коли("користувач {string} виконує запит пошуку {string} без параметрів")
    * Для першої змінної `string` необхідно вказати ім'я користувача кабінету посадової особи, яке буде вказано
      у `user.json` файлі
    * Для другої змінної `string` необхідно вказати endpoint на який буде відправлено запит методом GET
    * Якщо ендпоінт має ідентифікатор отриманий в попередніх кроках, то він передається в фігурних дужках

```gherkin 
    Коли користувач "auto-user" виконує запит пошуку "users/{userId}" без параметрів
    Та користувач "auto-user" виконує запит пошуку "orders" без параметрів
```

* @Тоді("результат запиту {string} містить наступні значення {string} у полі {string}")
    * Для першої змінної `string` необхідно вказати назву запиту
    * Для другої змінної`string` необхідно вказати назву параметра із тіла відповіді
    * Для третьої змінної `string` необхідно вказати значення параметра із тіла відповіді  
      Приклад:

```gherkin 
      Тоді результат запиту "school-by-year" містить наступні значення "2022" у полі "year"
```

* @Тоді("дата модель повертає точно заданий json нижче:")
    * Нижче цього кроку необхідно у вигляді строки вказати очікувані дані отримані у відповідь на запит    
      Приклад:

```gherkin
      Тоді дата модель повертає точно заданий json нижче:
      | [{"cnt":1,"externalId":173268}] |
```

* @Тоді("дата модель за запитом {string} повертає json з файлу {string}")
    * Для першої змінної `string` необхідно вказати назву запиту
    * Для другої змінної `string` необхідно вказати шлях до json файлу із очікуваним результатом 
      Приклад:

```gherkin 
     Тоді дата модель за запитом "status-name-read-all" повертає json з файлу "features/data/search-conditions/status/status_name_read_all.json"
```

* @Тоді("дата модель за запитом {string} повертає json, який містить точно наступні дані, ігноруючі невказані:")
  * Для змінної `string` необхідно вказати назву запиту
  * Нижче цього кроку необхідно у вигляді строки вказати очікувані дані отримані у відповідь на запит  
        Приклад:

```gherkin 
     Тоді дата модель за запитом "type-read-all" повертає json, який містить точно наступні дані, ігноруючі невказані:
      | [{"code": "TYPE", "name": "Тип"}] |
```  

* @Тоді("дата модель за запитом {string} повертає точно заданий json з файлу {string}, ігноруючі невказані")
  * Для першої змінної `string` необхідно вказати назву запиту 
  * Для другої змінної `string` необхідно вказати шлях до json файлу із очікуваним результатом
    Приклад:

```gherkin 
     Тоді дата модель за запитом "type-read-all" повертає точно заданий json з файлу "features/data/search-conditions/type/type_read_all.json", ігноруючі невказані
``` 

* @Тоді("дата модель за запитом {string} повертає точно заданий json з файлу {string}, відсортований по полю {string} ігноруючі невказані")
    * Для першої змінної `string` необхідно вказати назву запиту
    * Для другої змінної `string` необхідно вказати шлях до json файлу із очікуваним результатом
    * Для третьої змінної `string` необхідно вказати назву поля, за яким відсортовано запит
      Приклад:

```gherkin 
     дата модель за запитом "address-detailed-search" повертає точно заданий json з файлу "features/data/search-conditions/address-detailed-search/address_name_contains_morske.json", відсортований по полю "name" ігноруючі невказані
```  

* @Тоді("дата модель за запитом {string} повертає json з файлу {string}, відсортований по полю {string}")
  * Для першої змінної `string` необхідно вказати назву запиту
  * Для другої змінної `string` необхідно вказати шлях до json файлу із очікуваним результатом
  * Для третьої змінної `string` необхідно вказати назву поля, за яким відсортовано запит
    Приклад:

```gherkin 
     дата модель за запитом "address-detailed-search" повертає json з файлу "features/data/search-conditions/address-detailed-search/address_name_contains_morske.json", відсортований по полю "name"
```  

#### Кроки, які використовуються для перевірки розгорнутих бізнес-процесів та форм регламенту реєстру

* @Дано("регламент реєстру розгорнуто")
* @Коли("адміністратор регламенту {string} отримує наявні бізнес-процеси та відповідні їм форми через сервіси
  платформи")
    * Для змінної `string` необхідно вказати ім'я адміністратора реєстру, яке буде вказано у `user.json` файлі
      Приклад `user.json` файлу наведено нижче

```
{
   "auto-user": {
    "login": "",
    "realm": "-{realmNameSuffix}",
    "clientId": "",
    "realmRoles": [
      ""
    ],
    "realmClientsDictionary": {
      "admin": [
        {
          "clientId": ""
        }
      ]
    },
    "attributes": {}
  }
}
``` 

* @Тоді("він переконується, що бізнес-процеси та їх форми доступні кінцевому користувачу")

#### Кроки, які використовуються для перевірки бізнес-процесів у кабінетах посадової особи та користувача послуг

## В абзаці передумов які виконуються перед запуском сценарію, або в кроках блоку Тоді використовуються наступні кроки:

* @Коли("користувач {string} виконує запит пошуку {string} з параметрами")
    * Для першої змінної `string` необхідно вказати ім'я користувача кабінету посадової особи, яке буде вказано
      у `user.json` файлі
    * Для другої змінної `string` необхідно вказати endpoint на який буде відправлено запит методом GET
    * Нижче цього кроку необхідно у вигляді таблиці вказати назву параметра та його значення
    * Якщо значення - це ідентфікатор, отриманий в результаті попередніх запитів, тоді значення має бути записано в
      фігурних дужках
      `| параметр | значення |`  
      Приклад:

```gherkin
     Коли користувач "auto-user" виконує запит пошуку "year-name-contains" з параметрами
       | name | 2022   |
       |userId|{userId}|
```

* @Коли("користувач {string} виконує запит пошуку {string} без параметрів")
    * Для першої змінної `string` необхідно вказати ім'я користувача кабінету посадової особи, яке буде вказано
      у `user.json` файлі
    * Для другої змінної `string` необхідно вказати endpoint на який буде відправлено запит методом GET
    * Якщо ендпоінт має ідентифікатор отриманий в попередніх кроках, то він передається в фігурних дужках
  
```gherkin 
    Коли користувач "auto-user" виконує запит пошуку "users/{userId}" без параметрів
    Та користувач "auto-user" виконує запит пошуку "orders" без параметрів
```

* @Коли("виконується фільтрація результатів запиту {string} за параметрами")
    * Для змінної `string` необхідно вказати endpoint результати якого треба відфільтрувати
    * Нижче цього кроку необхідно у вигляді таблиці вказати назву параметрів за якими хочемо фільтрувати та Їх значення.
      `| параметр | значення |`  
      Приклад:

```gherkin
     Та виконується фільтрація результатів запиту "organization" за параметрами
       | fullName | Назва   |
       | type     | Приватна|
```

* @Коли користувач {string} виконує запит створення {string} з тілом запиту
    * Для першої змінної `string` необхідно вказати ім'я користувача кабінету посадової особи, яке буде вказано
      у `user.json` файлі
    * Для другої змінної `string` необхідно вказати endpoint на який буде відправлено запит методом POST
    * Нижче цього кроку необхідно у вигляді таблиці вказати параметри тіла запиту з їх значеннями  
      `| параметр | значення |`  
    * Якщо значення має ідентифікатор отриманий в попередніх кроках, то він передається в фігурних дужках {}
    * Якщо значення має приймати список ідентифікаторів отриманих в попередніх кроках, то він передається в квадратних 
      дужках []
      Приклад:

```gherkin
     Та користувач "auto-user" виконує запит створення "organization" з тілом запиту
       | fullName | Назва     |
       | userIds  | [userId]  |
       | streetId | {streetId}|
```

* @Коли користувач {string} виконує запит створення {string} з тілом запиту та отримує успішний код відповіді
    * Для першої змінної `string` необхідно вказати ім'я користувача кабінету посадової особи, яке буде вказано
      у `user.json` файлі
    * Для другої змінної `string` необхідно вказати endpoint на який буде відправлено запит методом POST
    * Нижче цього кроку необхідно у вигляді таблиці вказати параметри тіла запиту з їх значеннями
      `| параметр | значення |`
    * Якщо значення має ідентифікатор отриманий в попередніх кроках, то він передається в фігурних дужках {}
    * Якщо значення має приймати список ідентифікаторів отриманих в попередніх кроках, то він передається в квадратних
      дужках []
      Приклад:

```gherkin
     Та користувач "auto-user" виконує запит створення "organization" з тілом запиту та отримує успішний код відповіді
       | fullName | Назва     |
       | userIds  | [userId]  |
       | streetId | {streetId}|
```

* @Коли користувач {string} виконує запит оновлення {string} з ідентифікатором {string} та тілом запиту
    * Для першої змінної `string` необхідно вказати ім'я користувача кабінету посадової особи, яке буде вказано
      у `user. json` файлі
    * Для другої змінної `string` необхідно вказати endpoint на який буде відправлено запит методом PUT
    * Для третьої змінної `string` необхідно вказати id сутності яка буде змінена  
    * Якщо значення - це ідентифікатор, отриманий в результаті попередніх запитів, тоді значення має бути записано в
      фігурних дужках
    * Нижче цього кроку необхідно у вигляді таблиці вказати параметри тіла запиту з їх значеннями  
      `| параметр | значення |`  
      Приклад:

```gherkin
     Та користувач "auto-user" виконує запит оновлення "organization" з ідентифікатором "{id}" та тілом запиту
       | fullName | Назва   |
       | type     | Приватна|
```

* @Тоді користувач {string} виконує запит {string} видалення даних створених в сценарії з назвою параметру {string}
    * Для першої змінної `string` необхідно вказати ім'я користувача кабінету посадової особи, яке буде вказано
      у `user. json` файлі
    * Для другої змінної `string` необхідно вказати endpoint на який буде відправлено запит методом DELETE
    * Для третьої змінної необхідно вказати назву параметра створеного в тесті за значенням якого треба виконати запит
      видалення Приклад:

```gherkin
     І користувач "auto-user" виконує запит "unit" видалення даних створених в сценарії з назвою параметру "unitId"
```

# Кроки отримання даних з суміжних реєстрів

* @Коли("користувач {string} виконує запит пошуку {string} в реєстрі {string} з параметрами")
    * Для першої змінної `string` необхідно вказати ім'я користувача кабінету посадової особи, яке буде вказано
      у `user.json` файлі
    * Для другої змінної `string` необхідно вказати endpoint на який буде відправлено запит методом GET
    * Для третьої змінної необхідно вказати назву реєстру куди піде запит пошуку
    * Нижче цього кроку необхідно у вигляді таблиці вказати назву параметра та його значення
    * Якщо значення - це ідентифікатор, отриманий в результаті попередніх запитів, тоді значення має бути записано в
      фігурних дужках
      `| параметр | значення |`  
      Приклад:

```gherkin
     Коли користувач "auto-user" виконує запит пошуку "year-name-contains" в реєстрі "reg-2" з параметрами
       | name | 2022   |
       |userId|{userId}|
```
* @Коли("користувач {string} виконує запит пошуку {string} в реєстрі {string} без параметрів")
    * Для першої змінної `string` необхідно вказати ім'я користувача кабінету посадової особи, яке буде вказано
      у `user.json` файлі
    * Для другої змінної `string` необхідно вказати endpoint на який буде відправлено запит методом GET
    * Для третьої змінної необхідно вказати назву реєстру куди піде запит пошуку
    * Якщо ендпоінт має ідентифікатор отриманий в попередніх кроках, то він передається в фігурних дужках
```gherkin
    Коли користувач "auto-user" виконує запит пошуку "users/{userId}" в реєстрі "reg-2" без параметрів
    Та користувач "auto-user" виконує запит пошуку "orders" в реєстрі "reg-2" без параметрів
```

## Безпосередньо в блоці тесту використовуються наступні кроки:

* @Дано("користувач {string} успішно увійшов у кабінет посадової особи")
    * Для змінної `string` необхідно вказати ім'я користувача кабінету посадової особи, яке буде вказано у `user.json`
      файлі
    * `user.json` файл необхідно додати до репозиторію `registry-tests` за наступним
      шляхом `\src\test\resources\properties\users.json`
    * дані атрибутів заповнюються згідно даних ключа, який необхідно додати до репозиторію `registry-tests` за наступним
      шляхом `\src\test\resources\files\keys\<keyFileName>.dat`   
      drfo, edrpou, fullName, name, password (конвертується у base64), provider  
      Приклад `user.json` файлу наведено нижче

```
    {
      "auto-user": {
      "login": "",
      "realm": "-{realmNameSuffix}",
      "clientId": "",
      "realmRoles": [
        "officer"
        ],
      "attributes": {
      "drfo": [
        ""
        ],
      "edrpou": [
        ""
        ],
      "fullName": [
        ""
        ]
      },
      "key": {
        "name": "<keyFileName>.dat",
        "password": "",
        "provider": ""
        }
    }
```
* @Дано("користувач переходить на головну сторінку кабінету")
* @Дано("переходить до розділу {section}")
    * Для змінної `section` необхідно вказати назву розділу, до якого переходить користувач
* @Дано("бачить доступну групу процесів {string}")
    * Для змінної `string` необхідно вказати назву групи процесів, де знаходиться процес, який тестується
* @Дано("бачить доступний процес {string}")
    * Для змінної `string` необхідно вказати назву процесу, який тестується
* @Дано("відкриває групу процесів {string}")
    * Для змінної `string` необхідно вказати назву групи процесів, де знаходиться процес, який тестується
* @Коли("користувач ініціює процес {string}")
    * Для змінної `string` необхідно вказати назву процесу, який тестується
* @Коли("бачить форму/сторінку {string}")
    * Для змінної `string` необхідно вказати назву форми, яку бачить користувач
* @Коли("бачить форму {string} із кнопкою {string} яка {booleanValue}")
    * Для першої змінної `string` необхідно вказати назву форми, яку бачить користувач
    * Для другої змінної `string` необхідно вказати назву кнопки на формі
    * Змінна `booleanValue` може приймати значення `активна` або `не активна`
* @Та("на формі {string} бачить наступний текст:")
    * Для змінної `string` необхідно вказати назву форми
* @Коли("бачить передзаповнені поля із даними$")
    * Дані для заповнення форми повинні бути додані в тесті у вигляді таблиці з наступною структурою
        * `| name | type | value |`
        * name - це назва елементу форми
        * type - тип елементу форми, можливі значення `radiobutton`, `RADIOBUTTON`, `checkbox`, `CHECKBOX`, `input`
          , `INPUT`, `select`, `SELECT`, `datetime`, `DATETIME`, `textarea`, `TEXTAREA`
        * value - дані, якими заповнено елемент на формі, якщо значення поля отримане в попередніх запитах, то воно передається в фігурних дужках
        * `| назва поля | тип поля | значення |`  
            Приклад:

```gherkin
     І бачить передзаповнені поля із даними
          | name             | type  | value    |
          | Ім'я користувача | input | {userId} |
```
* @Коли("користувач заповнює форму даними$")
    * Дані для заповнення форми повинні бути додані в тесті у вигляді таблиці з наступною структурою
        * `| name | type | value |`
        * name - це назва елементу форми
        * type - тип елементу форми, можливі значення `radiobutton`, `RADIOBUTTON`, `checkbox`, `CHECKBOX`, `input`
          , `INPUT`, `select`, `SELECT`, `datetime`, `DATETIME`, `textarea`, `TEXTAREA`
        * value - дані, якими необхідно заповнити елемент на формі, якщо значення поля отримане в попередніх запитах, то воно передається в фігурних дужках
        * `| назва поля | тип поля | значення |`  
          Приклад:

```gherkin
     І користувач заповнює форму даними
          | name             | type  | value    |
          | Ім'я користувача | input | {userId} |
```
* @І("додає запис до {string} таблиці із даними")
    * Для змінної `string` необхідно вказати назву таблиці (елемент editgrid на формі) яка заповнюється даними
    * Дані для заповнення таблиці повинні бути додані в тесті у вигляді таблиці з наступною структурою
          * `| name | type | value |`
          * name - це назва елементу форми
          * type - тип елементу форми, можливі значення `radiobutton`, `RADIOBUTTON`, `checkbox`, `CHECKBOX`, `input`
            , `INPUT`, `select`, `SELECT`, `datetime`, `DATETIME`, `textarea`, `TEXTAREA` 
          * value - дані, якими необхідно заповнити елемент на формі, якщо значення поля отримане в попередніх запитах, то воно передається в фігурних дужках
          * `| назва поля | тип поля | значення |`  
            Приклад:

```gherkin
     І додає запис до "Користувачі" таблиці із даними
          | name             | type  | value    |
          | Ім'я користувача | input | {userId} |
```
* @І("користувач генерує випадкові дані з {randomValueType} у кількості {int} та записує у змінну {string}")
    * Змінна `randomValueType` може приймати значення `цифр` або `літер`
    * Для змінної `int` необхідно вказати кількість випадкових символів
    * Для змінної `string` необхідно вказати назву змінної
* @І("встановлює точку з координатами x: {int} та y: {int} на мапі")
    * Для першої змінної `int` необхідно вказати координату x
    * Для другої змінної `int` необхідно вказати координату y
* @І("користувач додає файл {string} у поле {string}")
    * Для першої змінної `string` необхідно вказати назву файлу
    * Для другої змінної `string` необхідно вказати назву поля
* @Та("натискає кнопку {string}")
    * Для змінної `string` необхідно вказати назву кнопки, яку натискає користувач
* @Та("на формі {string} бачить повідомлення {string} з текстом:")
* @Коли("пересвідчується в правильному відображенні введених даних на формі {string}")
    * Для змінної `string` необхідно вказати назву форми підпису даних
* @Коли("підписує дані")
* @Тоді("завантажує витяг послуги {string} з ідентифікатором {string}")
    * Для першої змінної `string` необхідно вказати назву послуги
    * Для другої змінної `string` необхідно вказати ідентифікатор послуги
    * Якщо значення змінної, яка використовується в ідентифікаторі послуги, отримане в попередніх запитах, то воно передається в фігурних дужках
Приклад:

```gherkin
      Тоді завантажує витяг послуги "Формування витягу" з ідентифікатором "{excerptId} Сформовано витяг"
 ```
* @Тоді("витяг завантажено")
* @Тоді("витяг у форматі csv містить наступні дані:")
* @Тоді("процес закінчено успішно й задача {string} відображається як виконана у переліку задач")
    * Для змінної `string` необхідно вказати назву виконаної задачі
* @Тоді("задача {string} за послугою {string} з ідентифікатором {string} знаходиться у статусі: Задачі для виконання")
    * Для першої змінної `string` необхідно вказати назву задачі
    * Для другої змінної `string` необхідно вказати назву послуги
    * Для третьої змінної `string` необхідно вказати ідентифікатор задачі
    * Якщо значення змінної, яка використовується в ідентифікаторі задачі, отримане в попередніх запитах, то воно передається в фігурних дужках
  Приклад:

```gherkin
      Тоді задача "Збереження користувача" за послугою "Реєстрація користувача" з ідентифікатором "{userId} Іванов Іван" знаходиться у статусі: Задачі для виконання
 ```
* @Тоді("задача {string} за послугою {string} з ідентифікатором {string} виконана")
    * Для першої змінної `string` необхідно вказати назву задачі
    * Для другої змінної `string` необхідно вказати назву послуги
    * Для третьої змінної `string` необхідно вказати ідентифікатор задачі
    * Якщо значення змінної, яка використовується в ідентифікаторі задачі, отримане в попередніх запитах, то воно передається в фігурних дужках
      Приклад:

```gherkin
      Тоді задача "Збереження користувача" за послугою "Реєстрація користувача" з ідентифікатором "{userId} Іванов Іван" виконана
 ```
* @Тоді("приймає задачу {string} за послугою {string} з ідентифікатором {string}")
    * Для першої змінної `string` необхідно вказати назву задачі
    * Для другої змінної `string` необхідно вказати назву послуги
    * Для третьої змінної `string` необхідно вказати ідентифікатор задачі
    *  Якщо значення змінної, яка використовується в ідентифікаторі задачі, отримане в попередніх запитах, то воно передається в фігурних дужках
       Приклад:

```gherkin
      Тоді приймає задачу "Збереження користувача" за послугою "Реєстрація користувача" з ідентифікатором "{userId} Іванов Іван"
```
* @Тоді("виконує задачу {string} за послугою {string} з ідентифікатором {string}")
    * Для першої змінної `string` необхідно вказати назву задачі
    * Для другої змінної `string` необхідно вказати назву послуги
    * Для третьої змінної `string` необхідно вказати ідентифікатор задачі
    * Якщо значення змінної, яка використовується в ідентифікаторі задачі, отримане в попередніх запитах, то воно передається в фігурних дужках
    Приклад:

```gherkin
      Тоді виконує задачу "Збереження користувача" за послугою "Реєстрація користувача" з ідентифікатором "{userId} Іванов Іван"
```
* @Тоді("послуга {string} з ідентифікатором {string} відображена в наданих послугах з результатом {string}")
    * Для першої змінної `string` необхідно вказати назву послуги
    * Для другої змінної `string` необхідно вказати ідентифікатор послуги
    * Якщо значення змінної, яка використовується в ідентифікаторі послуги, отримане в попередніх запитах, то воно передається в фігурних дужках
    * Для третьої змінної `string` необхідно вказати результат виконання послуги
    Приклад:

```gherkin
      Тоді послуга "Зареєструвати користувача" з ідентифікатором "{userId} Іванов Іван" відображена в наданих послугах з результатом "Користувач зареестрований"
 ```
* @Тоді("послуга {string} з ідентифікатором {string} знаходиться у статусі: Послуги у виконанні")
    * Для першої змінної `string` необхідно вказати назву послуги
    * Для другої змінної `string` необхідно вказати ідентифікатор послуги
    * Якщо значення змінної, яка використовується в ідентифікаторі послуги, отримане в попередніх запитах, то воно передається в фігурних дужках
    Приклад:

```gherkin
      Тоді послуга "Реєстрація користувача" з ідентифікатором "{userId Іванов Іван}" знаходиться у статусі: Послуги у виконанні
```

#### Приклад тесту для перевірки розгорнутої моделі даних та згенерованого запиту

```
Функціонал: Створити endpoint для заповнення поля 'Тип' з підтримкою EQUALS

 Структура сценарію: [EQUALS] Перевірка запиту пошуку за полем 'Тип' та порівняння значень в обраних параметрах результату
  Дано користувачу "auto-user" доступна розгорнута модель даних з переліком таблиць та згенерованими запитами доступу та пошуку даних
  Коли користувач "auto-user" виконує запит пошуку "type-equals" з параметрами
   | code | <code> |
  Тоді результат запиту містить наступні значення "<code>" у полі "code"
  А також результат запиту містить наступні значення "<type>" у полі "name"
  Приклади:
   | code         | type        |
   | ENROLLMENT   | Зарахування |
   | EXCLUSION    | Відрахування|
```

#### Приклад тесту для перевірки бізнес-процесу

```gherkin
Функціонал: Як уповноважена особа, я хочу мати можливість ініціювати процес

  Сценарій: [POSITIVE] Перевірка бізнес-процесу "Процес"
    Дано користувач "auto-user" успішно увійшов у кабінет посадової особи
    Та бачить доступний процес "Процес"
    Коли користувач ініціює процес "Процес"
    Та бачить форму "Внести дані про процес" із кнопкою "Далі" яка не активна
    І користувач заповнює форму даними
      | name   | type        | value      |
      | Назва  | input       | test       |
      | Рік    | select      | 2022-2023  |
      | Ознака | checkbox    | Ні         |
      | Тип    | radiobutton | Загальний  |
      | Дата   | datetime    | 2022-06-30 |
    І натискає кнопку "Далі"
    А також пересвідчується в правильному відображенні введених даних на формі "Підписати дані про процес"
    Та підписує їх
    Тоді процес закінчено успішно й задача "Підписати дані про процес" відображається як виконана у переліку задач
    Та бачить сторінку "Мої задачі"
```

#### Приклад тесту для розгортання регламенту

```
Функціонал: Розгортання регламенту  

 Сценарій: Перевірка успішного розгортання регламенту та доступності бізнес процесів та їх форм у реєстрі  
  Дано регламент реєстру розгорнуто  
  Коли адміністратор регламенту "auto-user-registry-regulation" отримує наявні бізнес процеси та відповідні їм форми через сервіси платформи  
  Тоді він переконується, що бізнес процеси та їх форми доступні кінцевому користувачу
```

### Test execution

* Tests could be run via maven command:
    * `mvn test` OR using appropriate functions of your IDE.

### License

The registry-steps is Open Source software released under
the [Apache 2.0 license](https://www.apache.org/licenses/LICENSE-2.0).

