plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.example.eduguard"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.example.eduguard"
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }
    buildFeatures {
        viewBinding=true
    }
    packagingOptions {
        resources {
            excludes += "META-INF/versions/9/OSGI-INF/MANIFEST.MF"
        }
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
}

dependencies {

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    implementation(libs.room.common.jvm)
    implementation(libs.room.runtime.android)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)

    implementation(libs.core.ktx)
// Lifecycle MVVM
    implementation(libs.lifecycle.viewmodel)
    implementation(libs.lifecycle.livedata)
// Navigation Component
    implementation(libs.navigation.fragment)
    implementation(libs.navigation.ui)
// Retrofit + JSON
    implementation(libs.retrofit)
    implementation(libs.converter.gson)
// DataStore (Recommended)
    implementation(libs.datastore.preferences)
// WorkManager (background tasks)
    implementation(libs.work.runtime)
// Optional debug tools
    implementation(libs.logging.interceptor)

    implementation(libs.material.v190)

    implementation(libs.datastore.preferences.v100)
    implementation(libs.kotlinx.coroutines.android)

    implementation (libs.room.runtime)
    annotationProcessor (libs.room.compiler)

    implementation(libs.mpandroidchart)
}