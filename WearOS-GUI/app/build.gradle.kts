plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.example.wearos_gui"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.wearos_gui"
        minSdk = 30
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

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
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}

dependencies {
    implementation(libs.play.services.wearable)
    implementation(libs.wear)
    implementation(libs.material)
}