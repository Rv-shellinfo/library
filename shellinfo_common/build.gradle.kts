plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
    kotlin("kapt")
    id("kotlin-parcelize")
}




android {
    namespace = "com.shellinfo.common"
    compileSdk = 33

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
            buildConfigField("String", "BASE_API_URL", "\"https://a0f93e1a8724476c9a447a3922c4f225.api.mockbin.io/\"")
            buildConfigField("String", "API_DOMAIN", "\"jsonplaceholder.typicode.com\"")
            buildConfigField("String", "CASH_FREE_DOMAIN", "\"https://sandbox.cashfree.com/pg/orders\"")
            buildConfigField("String", "CASH_FREE_NOTIFY_URL", "\"https://122.252.226.254:5114/api/v1/NotifyUrl/CFPaymentRequest\"")

        }

        release {
            isMinifyEnabled = true
            //signingConfig = signingConfigs.getByName("release")
            buildConfigField("String", "BASE_API_URL", "\"https://jsonplaceholder.typicode.com/\"")
            buildConfigField("String", "API_DOMAIN", "\"jsonplaceholder.typicode.com\"")
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
            buildConfigField("String", "SSL_FINGERPRINT", "\"fingerPrint\"")
            buildConfigField("String", "CASH_FREE_DOMAIN", "\"https://api.cashfree.com/pg/orders\"")
            buildConfigField("String", "CASH_FREE_NOTIFY_URL", "\"https://125.18.76.109:5114/api/v1/NotifyUrl/CFPaymentRequest\"")

        }
    }

    buildFeatures{
        buildConfig =true
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

}

//fataar{
//    transitive =true
//}


dependencies {

    implementation("androidx.core:core-ktx:1.10.1")
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.6.2")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.6.2")
    implementation("androidx.activity:activity-ktx:1.4.0")

    //hilt library for dependency injection
    implementation("com.google.dagger:hilt-android:2.46.1")
    kapt("com.google.dagger:hilt-android-compiler:2.46.1")

    // define a BOM and its version
    implementation(platform("com.squareup.okhttp3:okhttp-bom:4.9.0"))
    implementation("com.squareup.okhttp3:okhttp")
    implementation("com.squareup.okhttp3:logging-interceptor")


    //retrofit for the API calls
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

    implementation("com.sunmi:printerlibrary:1.0.18")

    implementation("com.cashfree.pg:api:2.1.9")


}



