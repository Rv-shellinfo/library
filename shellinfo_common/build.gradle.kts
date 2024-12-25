import org.gradle.api.publish.maven.MavenPublication
import org.gradle.kotlin.dsl.publishing

plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
    kotlin("kapt")
    id("kotlin-parcelize")
    id("maven-publish")
    id("com.google.dagger.hilt.android")

    id("com.vanniktech.maven.publish") version "0.28.0" apply false
    id("com.gradleup.nmcp") version "0.0.7" apply false
}

//apply(from = "publish.gradle")
//apply(from = "uploadLibrary.gradle")




android {
    namespace = "com.shellinfo.common"
    compileSdk = 34

    defaultConfig {
        minSdk = 24
    }


    packagingOptions{
        exclude("META-INF/LICENSE")
        exclude("META-INF/NOTICE")
        exclude("res/values/value.xml")
    }

    buildTypes {
        debug {
            isMinifyEnabled = false
            buildConfigField("String", "SSL_FINGERPRINT", "\"fingerPrint\"")
            buildConfigField("String", "BASE_API_URL", "\"https://a0f93e1a8724476c9a447a3922c4f225.implementation.mockbin.io/\"")
            buildConfigField("String", "API_DOMAIN", "\"jsonplaceholder.typicode.com\"")
            buildConfigField("String", "CASH_FREE_DOMAIN", "\"https://sandbox.cashfree.com/pg/orders\"")
            buildConfigField("String", "CASH_FREE_NOTIFY_URL", "\"https://122.252.226.254:5114/implementation/v1/NotifyUrl/CFPaymentRequest\"")
            buildConfigField("String", "BUILD_VERSION", "\"v1.1.26\"")

        }

        release {
            isMinifyEnabled = false
            //signingConfig = signingConfigs.getByName("release")
            buildConfigField("String", "BASE_API_URL", "\"https://jsonplaceholder.typicode.com/\"")
            buildConfigField("String", "API_DOMAIN", "\"jsonplaceholder.typicode.com\"")
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
            buildConfigField("String", "SSL_FINGERPRINT", "\"fingerPrint\"")
            buildConfigField("String", "CASH_FREE_DOMAIN", "\"https://implementation.cashfree.com/pg/orders\"")
            buildConfigField("String", "CASH_FREE_NOTIFY_URL", "\"https://125.18.76.109:5114/implementation/v1/NotifyUrl/CFPaymentRequest\"")
            buildConfigField("String", "BUILD_VERSION", "\"v1.1.26\"")
        }
    }

    buildFeatures{
        buildConfig =true
        aidl= true
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }

    kapt {
        correctErrorTypes = true
    }

    sourceSets {
        getByName("main").java.srcDirs("src/main/java")
    }

}

publishing{

    publications{
        register<MavenPublication>("debug"){
            afterEvaluate{
                from(components["debug"])
            }
        }
    }
}

repositories {
    google()
    mavenCentral()
    maven {
        url = uri("https://repo.eclipse.org/content/repositories/paho-releases/")
        url = uri("https://jitpack.io")
        url = uri("https://raw.github.com/synergian/wagon-git/releases")
    }
}



// Apply the standalone publish.gradle script
//apply(from = "publish.gradle")


dependencies {

    implementation("androidx.core:core-ktx:1.10.1")
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.6.2")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.6.2")
    implementation("androidx.activity:activity-ktx:1.4.0")

    //hilt library for dependency injection
    implementation("com.google.dagger:hilt-android:2.46.1")
    implementation("androidx.hilt:hilt-work:1.2.0")
    kapt("com.google.dagger:hilt-android-compiler:2.46.1")
    kapt("androidx.hilt:hilt-compiler:1.2.0")

    // define a BOM and its version
    implementation(platform("com.squareup.okhttp3:okhttp-bom:4.9.0"))
    implementation("com.squareup.okhttp3:okhttp")
    implementation("com.squareup.okhttp3:logging-interceptor")


    //retrofit for the implementation calls
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.jakewharton.retrofit:retrofit2-kotlin-coroutines-adapter:0.9.2")
    implementation("com.squareup.retrofit2:converter-moshi:2.9.0")
    implementation("com.squareup.retrofit2:converter-scalars:2.9.0")

    // Moshi
    kapt("com.squareup.moshi:moshi-kotlin-codegen:1.14.0")
    implementation("com.squareup.moshi:moshi:1.14.0")
    implementation("com.squareup.moshi:moshi-kotlin:1.14.0")
    implementation("com.squareup.moshi:moshi-adapters:1.14.0")

    // Coroutines
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.4")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.6.4")

    //Room
    implementation("androidx.room:room-runtime:2.5.1")
    kapt("androidx.room:room-compiler:2.5.1")

    // optional - Kotlin Extensions and Coroutines support for Room
    implementation("androidx.room:room-ktx:2.5.1")

    // Timber
    implementation("com.jakewharton.timber:timber:5.0.1")

    //kotlin serialization
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.4.1")

    //kotlin rx extensions
    implementation("io.reactivex.rxjava2:rxjava:2.2.6")
    implementation("com.squareup.retrofit2:adapter-rxjava2:2.3.0")
    implementation("io.reactivex.rxjava2:rxandroid:2.1.1")

    //security
    implementation("androidx.security:security-crypto:1.1.0-alpha06")

    //barcode
    implementation("com.google.zxing:core:3.2.1")

    //sunmi printer library
    implementation("com.sunmi:printerlibrary:1.0.18")

    //cash free pg library
    implementation("com.cashfree.pg:api:2.1.9")

    //work manager
    implementation("androidx.work:work-runtime:2.9.0")

    //Log files
    implementation("com.github.aabolfazl:filelogger:1.0.4")

    //Ftp client library
    implementation("commons-net:commons-net:3.8.0")

    // mqtt client
    implementation("org.eclipse.paho:org.eclipse.paho.client.mqttv3:1.2.5")
    implementation("androidx.localbroadcastmanager:localbroadcastmanager:1.1.0")
    implementation("androidx.legacy:legacy-support-v4:1.0.0")
    implementation("com.github.hannesa2:paho.mqtt.android:3.3.5")

    //date time library for backward comaptibility
    implementation("com.jakewharton.threetenabp:threetenabp:1.3.1")

    //debug database library
    debugImplementation("com.github.amitshekhariitbhu.Android-Debug-Database:debug-db:1.0.7")

    //apk installer
    implementation("ru.solrudev.ackpine:ackpine-core:0.5.1")
    implementation("ru.solrudev.ackpine:ackpine-ktx:0.5.1")


    //ntp for clock sync
    implementation("com.github.instacart.truetime-android:library:3.5")

}





