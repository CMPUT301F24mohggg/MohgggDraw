plugins {
    id("com.android.application")
    id("com.google.gms.google-services")
}


android {
    namespace = "com.example.mohgggdraw"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.mohgggdraw"
        minSdk = 24
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    buildFeatures {
        viewBinding = true
    }
}

dependencies {

    implementation(platform("org.jetbrains.kotlin:kotlin-bom:1.8.0"))

    implementation(platform("com.google.firebase:firebase-bom:31.0.2"))
    implementation("com.google.firebase:firebase-firestore:24.1.2")
    implementation("com.google.firebase:firebase-storage:20.0.0")
    implementation("com.google.firebase:firebase-database:20.1.0")
    implementation("com.github.bumptech.glide:glide:4.13.2")


    implementation("com.journeyapps:zxing-android-embedded:4.3.0")

    implementation(libs.androidx.fragment.testing)
    testImplementation(libs.androidx.espresso.core)
    testImplementation(libs.androidx.core)
    testImplementation(libs.androidx.junit)
    implementation(libs.fragment.testing)
    implementation(libs.espresso.intents)

    annotationProcessor("com.github.bumptech.glide:compiler:4.13.2")

    // Navigation dependencies
    implementation("androidx.navigation:navigation-fragment-ktx:2.3.5")
    implementation("androidx.navigation:navigation-ui-ktx:2.3.5")

    implementation("androidx.appcompat:appcompat:1.3.1")
    implementation("com.google.android.material:material:1.4.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.0")
    implementation("com.google.android.material:material:1.7.0") // Check for latest version
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.5.1")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.5.1")

    // Testing dependencies
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    androidTestImplementation ("androidx.test:runner:1.1.1")
   

    // Use Hamcrest version 1.3 to match Espresso's requirements
    testImplementation("org.hamcrest:hamcrest-library:1.3")
    androidTestImplementation("org.hamcrest:hamcrest-library:1.3")
}
