plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.example.cropconnect"
    compileSdk = 37

    defaultConfig {
        applicationId = "com.example.cropconnect"
        minSdk = 26
        targetSdk = 36
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
}

dependencies {
    // Core Android
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.constraintlayout)
    implementation(libs.activity.ktx)

    // OSMDroid for maps
    implementation(libs.osmdroid.android)

    // Retrofit for API calls
    implementation(libs.retrofit)
    implementation(libs.converter.gson)

    // OkHttp for HTTP logging
    implementation(libs.okhttp)
    implementation(libs.logging.interceptor)

    //Security.Crypto for the JWT token implementation
    implementation(libs.security.crypto)
}