plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    id("kotlin-kapt") // Added kotlin-kapt
}

android {
    namespace = "com.example.speechtotext"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.example.speechtotext"
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        buildFeatures.buildConfig = true
        buildConfigField("String", "OPENAI_API_KEY", "") // Add your API key here

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
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.material)
    implementation(libs.androidx.compose.material.iconsExtended)

    // Speech-to-Text (Android's built-in Recognizer doesn't need a specific library here)

    // AI Summarization (Networking & AI SDK)
    // Using Google AI SDK for Gemini (recommended for Gemini)
    implementation("com.google.ai.client.generativeai:generativeai:0.1.0") // Check for latest version

    // Retrofit for other HTTP requests (if needed, e.g. other APIs or if not using Gemini SDK directly for everything)
    implementation("com.squareup.retrofit2:retrofit:2.9.0") // Check for latest version
    implementation("com.squareup.retrofit2:converter-gson:2.9.0") // Check for latest version
    implementation("com.squareup.okhttp3:logging-interceptor:4.12.0") // Check for latest version

    // Data Storage (Room Database)
    implementation("androidx.room:room-runtime:2.6.1") // Check for latest version
    implementation("androidx.room:room-ktx:2.6.1") // For Kotlin Coroutines & Flow support, check for latest
    kapt("androidx.room:room-compiler:2.6.1") // Changed to kapt, check for latest version

    // Architecture Components & Asynchronous Operations
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.7.0") // Check for latest
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3") // Check for latest

    // Testing
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
}