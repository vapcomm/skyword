import java.io.FileInputStream
import java.util.*

plugins {
    id("com.android.application")
    kotlin("android")
    id("kotlin-android-extensions")
    id("kotlin-kapt")
}

android {
    compileSdkVersion(30)
    buildToolsVersion("30.0.1")
    buildFeatures.dataBinding = true

    defaultConfig {
        applicationId = "online.vapcom.skyword"
        minSdkVersion(23)
        targetSdkVersion(30)
        versionCode = 10

        versionName = "1.0.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        setProperty("archivesBaseName", "skyword-$versionName.$versionCode")
    }

    // конфиг подписи релизного APK-файла, параметры читаются из keystore.properties
    signingConfigs {
        create("release") {

            // keystorePropertiesFilename задан в gradle.properties или в командной строке, см. пример ниже
            val keystorePropertiesFile = rootProject.file(properties["keystorePropertiesFilename"] ?: "ks properties filename not found")
            val keystoreProperties = Properties()
            if (keystorePropertiesFile.exists()) {
                keystoreProperties.load(FileInputStream(keystorePropertiesFile))
            } else {
                println ("Cannot find keystore properties file.\n" +
                        "A) Create this file, B) use a different file, or C) use assembleDebug.\n" +
                        "A) Create ${keystorePropertiesFile.absolutePath}\n" +
                        "B) ./gradlew -PkeystorePropertiesFilename=release-keystore.properties assembleRelease\n" +
                        "C) ./gradlew assembleDebug\n")
            }

            storeFile = rootProject.file(keystoreProperties.getProperty("storeFile"))
            storePassword = keystoreProperties.getProperty("storePassword")
            keyAlias = keystoreProperties.getProperty("keyAlias")
            keyPassword = keystoreProperties.getProperty("keyPassword")
            isV1SigningEnabled = true
            isV2SigningEnabled = true

        }
    }

    buildTypes {
        getByName("release") {
            //NOTE: т.к. это тестовое задание, то сделаю здесь ремарку, что этот якобы релизный signingConfig - это honeypot,
            //      который нужен для отвлечения внимания мамкиных хакеров, укравших исходники.
            //      Настоящая релизная сборка подписывается сертификатом, который лежит в JKS-файле на секретной флэшке.
            //      Местный skyword.keystore.jks используется только для подписи debug-сборок и на Google Play никогда не попадёт.

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
    flavorDimensions("versions")
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


}

// Включает целевую платформу 1.8, иначе будет ошибка
// Cannot inline bytecode built with jvm target 1.8 into bytecode that is being built with jvm target 1.6
// см. https://coil-kt.github.io/coil/getting_started/#java-8
// см. https://stackoverflow.com/questions/48988778/cannot-inline-bytecode-built-with-jvm-target-1-8-into-bytecode-that-is-being-bui
tasks.withType < org.jetbrains.kotlin.gradle.tasks.KotlinCompile > {
    kotlinOptions.jvmTarget = "1.8"
}


dependencies {
    //implementation (fileTree(dir: "libs", include: ["*.jar"]))

    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8:1.3.72")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.3.6")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.3.6")
    androidTestImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.3.3")

    implementation("androidx.core:core-ktx:1.3.0")
    implementation("androidx.constraintlayout:constraintlayout:1.1.3")
    implementation("androidx.lifecycle:lifecycle-extensions:2.2.0")

    implementation("androidx.navigation:navigation-fragment-ktx:2.3.0")
    implementation("androidx.navigation:navigation-ui-ktx:2.3.0")

    implementation("com.google.android.material:material:1.2.0-beta01")

    // см. свежие релизы OkHttp здесь https://square.github.io/okhttp/changelog/
    implementation("com.squareup.okhttp3:okhttp:4.8.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.8.0")

    implementation("com.squareup.moshi:moshi:1.9.2")
    implementation("com.squareup.moshi:moshi-kotlin:1.9.2")
    kapt("com.squareup.moshi:moshi-kotlin-codegen:1.9.2")

    // загрузка картинок https://github.com/coil-kt/coil
    implementation("io.coil-kt:coil:0.11.0")


    testImplementation("junit:junit:4.12")
    androidTestImplementation("androidx.test:runner:1.2.0")
    androidTestImplementation("androidx.test:core:1.2.0")
    androidTestImplementation("androidx.test.ext:junit:1.1.1")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.2.0")

    // Testing code should not be included in the main code.
    // Once https://issuetracker.google.com/128612536 is fixed this can be fixed.
    // если определять в androidTestImplementation, то получаем исключение Unable to resolve activity for: Intent
    debugImplementation("androidx.fragment:fragment-testing:1.2.5")

}
