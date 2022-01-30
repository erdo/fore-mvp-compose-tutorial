plugins {
    id("com.android.application")
    kotlin("android")
    kotlin("kapt")
    id("org.jetbrains.kotlin.plugin.serialization")
}

val appId = "foo.bar.compose"
val composeVersion = rootProject.extra["composeVersion"] as String

android {

    compileSdk = 31

    defaultConfig {
        applicationId = appId
        minSdk = 21
        targetSdk = 31
        versionCode = 1
        versionName = "1.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        compose = true
        viewBinding = true
    }
    composeOptions {
          kotlinCompilerExtensionVersion = composeVersion
    }
}

dependencies {

    // reactivity
    implementation("co.early.fore:fore-kt-android:1.5.8")
    implementation("co.early.fore:fore-kt-android-compose:$composeVersion")

    // persistence
    implementation("co.early.persista:persista:1.0.0")

    // compose
    implementation("androidx.compose.ui:ui:$composeVersion")
    implementation("androidx.compose.material:material:$composeVersion")
    implementation("androidx.activity:activity-compose:1.4.0")
    implementation("androidx.compose.ui:ui-tooling:$composeVersion")
    debugImplementation("androidx.compose.ui:ui-tooling-preview:$composeVersion")

    // design
    implementation("com.google.android.material:material:1.5.0")

    // networking
    implementation("io.ktor:ktor-client-serialization:1.5.2")
    implementation("io.ktor:ktor-client-okhttp:1.5.2")

    //testing
    testImplementation("junit:junit:4.13.2")
    testImplementation("io.mockk:mockk:1.11.0")
}
