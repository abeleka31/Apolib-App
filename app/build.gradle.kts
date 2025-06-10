// app/build.gradle.kts

plugins {
    alias(libs.plugins.android.application)
    id("com.google.gms.google-services") // Ini untuk menerapkan plugin
}

android {
    namespace = "com.example.apodicty"
    compileSdk = 35
    buildFeatures {
        viewBinding = true
    }

    defaultConfig {
        applicationId = "com.example.apodicty"
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

    // ================================
    // 📦 Firebase (menggunakan BoM)
    // ================================
    implementation(platform(libs.firebase.bom))
    implementation("com.google.firebase:firebase-auth")
    implementation("com.google.android.gms:play-services-auth")
    implementation("com.google.firebase:firebase-firestore")

    // ================================
    // 🎨 UI & Material Design
    // ================================
    implementation(libs.material) // Referensi dari libs.versions.toml
    implementation("com.google.android.material:material:1.12.0") // Versi terbaru, jika ingin eksplisit
    implementation(libs.appcompat)
    implementation("androidx.appcompat:appcompat:1.6.1") // Bisa diganti dengan libs.appcompat jika sudah ada
    implementation("androidx.core:core:1.12.0")
    implementation(libs.constraintlayout)
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation(libs.viewpager2)
    implementation("androidx.recyclerview:recyclerview:1.3.2")

    // ================================
    // 🧭 Navigation & Lifecycle
    // ================================
    implementation(libs.androidx.navigation.fragment)
    implementation(libs.androidx.navigation.ui)
    implementation(libs.androidx.annotation)
    implementation(libs.androidx.lifecycle.livedata.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.ktx)
    implementation("androidx.lifecycle:lifecycle-viewmodel:2.6.2")
    implementation("androidx.lifecycle:lifecycle-livedata:2.6.2")
    implementation(libs.activity)
    implementation(libs.androidx.legacy.support.v4)

    // ================================
    // 🔌 Networking (Retrofit, OkHttp)
    // ================================
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation("com.squareup.okhttp3:okhttp:4.9.1")
    implementation("com.squareup.okhttp3:logging-interceptor:4.11.0") // Logging Interceptor

    // ================================
    // 🖼️ Image Loading
    // ================================
    implementation("com.squareup.picasso:picasso:2.71828")
    implementation("com.github.bumptech.glide:glide:4.16.0")
    annotationProcessor("com.github.bumptech.glide:compiler:4.16.0")

    // ================================
    // 🧪 Testing
    // ================================
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
}
