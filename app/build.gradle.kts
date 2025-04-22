plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    id("com.google.gms.google-services")
}

android {
    namespace = "com.example.thirstyquest"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.thirstyquest"
        minSdk = 26
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
    implementation(libs.androidx.navigation.runtime.ktx)
    implementation(libs.navigation.compose)
    implementation("androidx.navigation:navigation-compose:2.8.9")
    implementation("androidx.compose.material:material-icons-extended:1.7.8")
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
    implementation("com.google.accompanist:accompanist-pager:0.30.1")
    implementation("io.coil-kt:coil-compose:2.1.0")
    implementation("com.google.accompanist:accompanist-drawablepainter:0.35.0-alpha")
    implementation("androidx.core:core-ktx:1.15.0")
    implementation("androidx.compose.runtime:runtime-livedata:1.7.8")
    implementation("com.google.firebase:firebase-storage:20.2.0")
    implementation("androidx.datastore:datastore-preferences:1.0.0")
    implementation("androidx.work:work-runtime-ktx:2.9.0")


    // For charts
    implementation("co.yml:ycharts:2.1.0")

    // Import the Firebase BoM
    implementation(platform("com.google.firebase:firebase-bom:33.11.0"))
    // When using the BoM, don't specify versions in Firebase dependencies
    implementation("com.google.firebase:firebase-analytics")
    // Add the dependency for the Firebase Authentication library
    // When using the BoM, you don't specify versions in Firebase library dependencies
    implementation("com.google.firebase:firebase-auth")
    implementation("com.google.firebase:firebase-firestore-ktx:24.9.1")

}
