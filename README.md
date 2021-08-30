﻿# Задание
Реализовать приложение для поиска переводов слов в словаре, состоящее из двух экранов:
- Экран, содержащий форму поиска слова и таблицу отображения результатов
- Экран, отображающий подробную информацию о слове (текст, перевод и картинка, остальные поля по желанию), открывается по нажатию на ячейку в таблице с результатами поиска

Загрузку данных о словах производить при помощи публичного API, документация находится здесь: https://dictionary.skyeng.ru/doc/api/external

Требования:
- Для Android использовать Kotlin
- К коду приложить Readme, где коротко описывается как собрать и запустить код
- Результат выполнения загрузить на любой Git-хостинг и прислать ссылку, zip-файлы и прочие форматы не принимаются
- Использование сторонних библиотек разрешается, в этом случае в Readme нужно указать зачем она используется
- Приложение должно корректно обрабатывать повороты экрана
- К приложению должен быть написан хотя бы 1 юнит-тест
- Реализованная функциональность должна быть максимально близка к production-состоянию
- UI приложения должен соответствовать гайдлайнам платформы.


# Сборка приложения

Из командной строки:
```sh
 ./gradlew :app:assembleDevDebug
 ```
 
В Android Studio: 
1. File/Open - открыть проект
2. Build/Make project
 
Для сборки релизного варианта APK:
1. Build/Generate Signed Bundle/APK
2. Выбрать APK/Next
3. Создать хранилище сертификата подписи APK. Тот, что лежит в skyword.keystore.jks используется только для отладочных сборок.
4. Дальше следовать мастеру сборки.
5. Посмотреть содержимое APK инструментами Студии.

В рамках этого тестового задания не производилась настройка proguard-файлов.

# Запуск приложения из Android Studio
1. Создать конфигурацию запуска 'app'.
2. Выбрать устройство или эмулятор из списка, при необходимости создать новый.
3. Нажать на кнопку запуска в виде зелёного треугольника, либо выбрать в меню Run/Run 'app'.

# Запуск приложения из собранного APK
Смотрите инструкцию по установке сторонних APK-файлов для вашего устройства.

# Сторонние библиотеки (не входящие в Android SDK)
* kotlinx-coroutines - реализация корутин в Kotlin
* OkHTTP - реализация HTTPS-клиента, для загрузки данных с сервера
* Moshi - удобный и компактный десереализатор JSON-объектов
* Coil - библиотека для загрузки картинок 
* JUnit - фреймворк модульного тестирования

# TODO на следующий релиз:
- иконка приложения во всех размерах
- лейауты для планшетов
- группировать найденные значения слов по их части речи
- показывать варианты слов при вводе слова в выпадающем списке
- показывать мнемонику с обработой тегов выделения слов
- сохранение и отображение списка ранее введённых слов (история поиска)
- озвучка слов
- кэш поиска в репозитории

