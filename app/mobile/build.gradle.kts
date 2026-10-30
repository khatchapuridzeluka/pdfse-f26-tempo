plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}

android {
    namespace = "com.tempo.helmetdemo"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.tempo.helmetdemo"
        minSdk = 26
        targetSdk = 35
        versionCode = 1
        versionName = "0.1-demo"

        // Real phones only; keeps the APK small.
        ndk { abiFilters += listOf("arm64-v8a", "armeabi-v7a") }
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            // Demo APK only: signed with the local debug key so anyone can build and install it.
            signingConfig = signingConfigs.getByName("debug")
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }

    // Models are memory-mapped from the APK, so they must stay uncompressed.
    androidResources {
        noCompress += "tflite"
    }
}

dependencies {
    implementation("androidx.core:core-ktx:1.13.1")
    implementation("androidx.activity:activity-ktx:1.9.3")

    // Versions with 16 KB page-aligned native libraries (required on Android 15+ devices).
    val camerax = "1.4.2"
    implementation("androidx.camera:camera-core:$camerax")
    implementation("androidx.camera:camera-camera2:$camerax")
    implementation("androidx.camera:camera-lifecycle:$camerax")
    implementation("androidx.camera:camera-view:$camerax")

    // Person detection (EfficientDet-Lite0, COCO)
    implementation("com.google.mediapipe:tasks-vision:0.10.29")
    // Helmet / vest detection (Qualcomm GearGuardNet)
    implementation("com.google.ai.edge.litert:litert:1.4.0")
}
