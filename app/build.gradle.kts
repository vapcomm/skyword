import java.io.FileInputStream
import java.util.*

plugins {
    id("com.android.application")
    kotlin("android")
    id("kotlin-android-extensions")
    id("kotlin-kapt")
}

android {
    compileSdk = 31
    buildToolsVersion = "31.0.0"
    buildFeatures.dataBinding = true

    defaultConfig {
        applicationId = "online.vapcom.skyword"
        minSdk = 23
        targetSdk = 31
        versionCode = 13

        versionName = "1.0.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        setProperty("archivesBaseName", "skyword-$versionName.$versionCode")
    }

    // конфиг подписи релизного APK-файла, параметры читаются из keystore.properties
    signingConfigs {
        create("release") {
            //NOTE: БОЛЬШИМИ БУКВАМИ, ДЛЯ ТЕХ, КТО В ТЕМЕ:
            // jks-файл с ключами подписи приложения, который тут рядом лежит, предназначен исключительно для подписи
            // отладочных APK-файлов. Для релизных сборок сделайте отдельный jks-файл и храните его отдельно от этих исходников.

            // keystorePropertiesFilename задан в gradle.properties или в командной строке, см. пример ниже
            val props = getProperties()
            val keystoreFilename = props["keystorePropertiesFilename"]
            if(keystoreFilename == null)
                throw InvalidUserDataException("Keystore properties filename not found")

            val keystorePropertiesFile = rootProject.file(keystoreFilename)
            val keystoreProperties = Properties()
            if (keystorePropertiesFile.exists()) {
                keystoreProperties.load(FileInputStream(keystorePropertiesFile))
            } else {
                println ("Error: Cannot find keystore properties file.\n" +
                        "A) Create this file, B) use a different file, or C) use assembleDebug.\n" +
                        "A) Create ${keystorePropertiesFile.absolutePath}\n" +
                        "B) ./gradlew -PkeystorePropertiesFilename=release-keystore.properties assembleRelease\n" +
                        "C) ./gradlew assembleDebug\n")
                throw InvalidUserDataException("Cannot find keystore properties file")
            }

            storeFile = rootProject.file(keystoreProperties.getProperty("storeFile"))
            storePassword = keystoreProperties.getProperty("storePassword")
            keyAlias = keystoreProperties.getProperty("keyAlias")
            keyPassword = keystoreProperties.getProperty("keyPassword")
            enableV1Signing = true
            enableV2Signing = true
            enableV3Signing = true
            enableV4Signing = true
        }
    }

    buildTypes {
        getByName("release") {
            //NOTE: включить, когда будет делаться релиз и прочитайте NOTE выше про jks-файл
            //signingConfig = signingConfigs.getByName("release")

            isMinifyEnabled = true
            proguardFiles ("proguard-rules.pro", "moshi.pro", "moshi-kotlin.pro", getDefaultProguardFile("proguard-android.txt"))
            // versionNameSuffix = "-release"
        }
        getByName("debug") {
            signingConfig = signingConfigs.getByName("release")
            buildConfigField("Boolean", "HTTP_LOGS_ON", "true")

            versionNameSuffix = "-debug"
        }
    }

    // конфигурации версий приложения
    flavorDimensions.add("versions")
    productFlavors {

        // боевые сервера
        create("prod") {
            versionNameSuffix = "-prod"
            // боевой  сервер словаря
            buildConfigField("String", "SERVER_DICT", "\"https://dictionary.skyeng.ru/api/public/v1\"")
        }

        // тестовые сервера
        create("dev") {
            versionNameSuffix = "-dev"
            applicationIdSuffix = ".dev"

            // тестовый сервер словаря
            //NOTE: в этом тестовом задании он такой же как и боевой
            buildConfigField("String", "SERVER_DICT", "\"https://dictionary.skyeng.ru/api/public/v1\"")
        }

        // для отладки на тестовом сервере tasty на локальной машине, приложение запускается в эмуляторе
        //NOTE: в этом тестовом задании я не разрабатывал свой бэк-сервер, эта конфигурация приведена для примера
        //      того, что я могу сделать локальный ktor-сервер для отладки приложения пока бэки не созрели до тестовых серверов.
        create("tasty") {
            versionNameSuffix = "-tasty"
            applicationIdSuffix = ".dev"

            // для работы на голом HTTP в манифесте включить
            // android:usesCleartextTraffic="true"

            buildConfigField("String", "SERVER_DICT", "\"http://10.0.2.2:8080\"")
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    packagingOptions {
        resources.excludes.add("META-INF/AL2.0")
        resources.excludes.add("META-INF/LGPL2.1")
        resources.excludes.add("META-INF/INDEX.LIST")
        resources.excludes.add("META-INF/io.netty.versions.properties")
        resources.excludes.add("license/README.dom.txt")
        resources.excludes.add("license/LICENSE.dom-documentation.txt")
        resources.excludes.add("license/LICENSE.dom-software.txt")
        resources.excludes.add("license/NOTICE")
        resources.excludes.add("license/LICENSE")
    }
}

dependencies {
    //implementation (fileTree(dir: "libs", include: ["*.jar"]))

    implementation("org.jetbrains.kotlin:kotlin-stdlib:1.5.30")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.5.1")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.5.1")
    androidTestImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.5.0")

    implementation("androidx.core:core-ktx:1.6.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.0")
    implementation("androidx.lifecycle:lifecycle-extensions:2.2.0")

    implementation("androidx.navigation:navigation-fragment-ktx:2.3.5")
    implementation("androidx.navigation:navigation-ui-ktx:2.3.5")

    implementation("com.google.android.material:material:1.5.0-alpha02")

    // см. свежие релизы OkHttp здесь https://square.github.io/okhttp/changelog/
    implementation("com.squareup.okhttp3:okhttp:4.9.1")
    implementation("com.squareup.okhttp3:logging-interceptor:4.9.1")

    implementation("com.squareup.moshi:moshi:1.12.0")
    implementation("com.squareup.moshi:moshi-kotlin:1.12.0")
    kapt("com.squareup.moshi:moshi-kotlin-codegen:1.12.0")

    // загрузка картинок https://github.com/coil-kt/coil
    implementation("io.coil-kt:coil:1.3.2")


    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test:runner:1.4.0")
    androidTestImplementation("androidx.test:core:1.4.0")
    androidTestImplementation("androidx.test.ext:junit:1.1.3")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.4.0")

    // Testing code should not be included in the main code.
    // Once https://issuetracker.google.com/128612536 is fixed this can be fixed.
    // если определять в androidTestImplementation, то получаем исключение Unable to resolve activity for: Intent
    debugImplementation("androidx.fragment:fragment-testing:1.4.0-alpha07")

}
