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
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    packaging {
        resources {
            excludes += setOf(
                "META-INF/LICENSE.md",
                "META-INF/LICENSE-notice.md",
                "META-INF/LICENSE.txt",
                "META-INF/NOTICE.md",
                "META-INF/NOTICE.txt"
            )
        }
    }
}

dependencies {
    // Core dependencies
    implementation(libs.play.services.wearable)
    implementation(libs.wear)
    implementation(libs.material)
    implementation(libs.jedis)
    implementation("com.google.android.gms:play-services-location:21.0.1")
    implementation(libs.appcompat)
    implementation(libs.activity)
    implementation(libs.constraintlayout)


    // JUnit 4 dependencies for Android Instrumentation tests
    androidTestImplementation(libs.junit)          // JUnit 4
    androidTestImplementation(libs.ext.junit)      // Android JUnit extensions
    androidTestImplementation(libs.core)           // AndroidX Test core
    androidTestImplementation("androidx.test:runner:1.6.2")  // Test runner
}
