import com.android.build.api.dsl.Packaging
import org.gradle.kotlin.dsl.implementation

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    id("kotlin-kapt")
}

android {
    namespace = "com.example.greenenergyapp"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.greenenergyapp"
        minSdk = 33
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        compose = true
        dataBinding = true

    }
    packaging {
        resources {
            excludes += "META-INF/NOTICE.md"
            excludes += "META-INF/LICENSE.md"
        }
    }
}

dependencies {
    implementation (libs.ui)

    implementation (libs.mail.android.mail)
    implementation (libs.android.activation)


    implementation(libs.androidx.material.icons.extended)
    // Room Database
    implementation (libs.androidx.room.runtime)
    implementation(libs.androidx.espresso.core)
    implementation(libs.androidx.runtime.livedata)

    // Room Compiler (Annotation Processing)
    kapt (libs.androidx.room.compiler)

    // Room Paging (isteğe bağlı)
    implementation (libs.androidx.room.paging)

    // SQLite (isteğe bağlı, Room zaten SQLite ile çalışır)
    implementation (libs.androidx.sqlite)

    // Coroutine desteği (isteğe bağlı)
    implementation (libs.androidx.room.ktx)

    // Room test bağımlılığı (isteğe bağlı)
    testImplementation (libs.androidx.room.testing)

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)

    implementation ("com.google.android.gms:play-services-location:21.0.1")
    implementation ("androidx.navigation:navigation-compose:2.7.7")
    // Retrofit for API calls
    implementation ("com.squareup.retrofit2:retrofit:2.9.0")
    implementation ("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation ("com.google.code.gson:gson:2.10.1")


    // Network and JSON parsing
    implementation("com.squareup.okhttp3:okhttp:4.11.0")
    implementation("com.google.code.gson:gson:2.10.1")
    // Coroutines
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.6.2")

    // Optional - logging interceptor for debugging API calls
    implementation ("com.squareup.okhttp3:logging-interceptor:4.11.0")

    implementation ("com.squareup.retrofit2:converter-gson:2.9.0")


    implementation ("com.squareup.retrofit2:retrofit:2.9.0")
    implementation ("com.squareup.retrofit2:converter-moshi:2.9.0"  ) // or converter‑gson
    implementation ("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.6.0")
}