plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.example.coba_aplikasi"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.coba_aplikasi"
        minSdk = 26
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

    buildFeatures {
        viewBinding = true
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}

dependencies {
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
    implementation ("androidx.viewpager2:viewpager2:1.0.0")
    implementation ("androidx.recyclerview:recyclerview:1.2.1")
    implementation ("androidx.cardview:cardview:1.0.0")
    implementation ("com.google.android.material:material:1.4.0")
    implementation ("androidx.constraintlayout:constraintlayout:2.1.3")
    implementation ("androidx.core:core-ktx:1.6.0")
    implementation ("com.github.VishnuSivadasVS:Advanced-HttpURLConnection:1.2")
    implementation ("com.android.volley:volley:1.2.1")




}
