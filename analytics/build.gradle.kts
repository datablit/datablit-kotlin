plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    `maven-publish`
}

android {
    namespace = "com.datablit.analytics"
    compileSdk = 35

    defaultConfig {
        minSdk = 26

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
    kotlinOptions {
        jvmTarget = "11"
    }
}

publishing {
    publications {
        create<MavenPublication>("release") {
            afterEvaluate { // Ensures Gradle configures the component properly
                from(components["release"])
            }

            groupId = "com.datablit.analytics"
            artifactId = "kotlin"
            version = "1.0.0"
        }
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    api("com.google.code.gson:gson:2.12.1")
    implementation("androidx.lifecycle:lifecycle-process:2.6.1")
    implementation("androidx.lifecycle:lifecycle-common-java8:2.6.1")
}