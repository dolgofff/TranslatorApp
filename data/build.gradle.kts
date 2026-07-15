plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)

    alias(libs.plugins.ksp)
    alias(libs.plugins.hilt)
    alias(libs.plugins.gms)

    alias(libs.plugins.jetbrains.kotlin.serialization)
    alias(libs.plugins.google.protobuf)
}

android {
    namespace = "com.example.data"
    compileSdk {
        version = release(36)
    }

    defaultConfig {
        minSdk = 24

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
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

    kotlin {
        compilerOptions {
            languageVersion = org.jetbrains.kotlin.gradle.dsl.KotlinVersion.KOTLIN_2_0
            jvmTarget = org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_11
        }
    }

    buildFeatures {
        buildConfig = true
    }
}

dependencies {
    implementation(project(":domain"))

    //ML kit dependencies
    implementation(libs.text.recognition)

    // Camera dependencies
    implementation(libs.camerax.core)
    implementation(libs.camerax.camera2)
    implementation(libs.camerax.lifecycle)
    implementation(libs.camerax.view)
    implementation(libs.androidx.concurrent.futures.ktx)

    // Firebase dependencies
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.firestore.ktx)
    implementation(libs.firebase.auth.ktx)
    implementation(libs.kotlinx.coroutines.play.services)

    //Proto DataStore
    //implementation(libs.google.protobuf)
    implementation(libs.androidx.datastore)

    // Hilt dependencies
    ksp(libs.hilt.compiler)
    implementation(libs.hilt.android)

    // Retrofit dependencies
    implementation(libs.retrofit)
    implementation(libs.retrofit.ser)
    implementation(libs.kotlinx.serialization.json)

    // OkHttp dependencies
    implementation(libs.okhttp.logging)
    implementation(libs.okhttp)

    // Audio player
    implementation(libs.androidx.exoplayer)

    // Serialization bug-fix
    implementation(libs.androidx.annotation.experimental)

    // Test dependencies
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}