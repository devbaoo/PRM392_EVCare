plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.example.prm392_evcare"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.prm392_evcare"
        minSdk = 24
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
}

dependencies {
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    
    // Retrofit for API calls
    implementation(libs.retrofit)
    implementation(libs.retrofit.gson)
    implementation(libs.okhttp)
    implementation(libs.okhttp.logging)
    implementation(libs.gson)
    
    // Glide for image loading
    implementation(libs.glide)
    
    // Lottie for animations
    implementation("com.airbnb.android:lottie:6.3.0")
    
    // Material Components
    implementation("com.google.android.material:material:1.11.0")
    
    // CardView
    implementation("androidx.cardview:cardview:1.0.0")
    
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
}