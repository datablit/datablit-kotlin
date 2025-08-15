import com.vanniktech.maven.publish.SonatypeHost

plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    `maven-publish`
    id("com.vanniktech.maven.publish") version "0.32.0"
}

android {
    namespace = "com.datablit"
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

/* local publish
Steps:
1. comment mavenPublishing block -> this is used for publishing to maven central and uncomment publishing
2. run ./gradlew publishToMavenLocal
3. also uncomment  //mavenLocal() from root settings.gradle.kts
4. once done revert all these
 */
//publishing {
//    publications {
//        create<MavenPublication>("release") {
//            afterEvaluate { // Ensures Gradle configures the component properly
//                from(components["release"])
//            }
//
//            groupId = "com.datablit"
//            artifactId = "kotlin"
//            version = "1.0.1"
//        }
//    }
//}

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

// central publish cmd: ./gradlew publishAllPublicationsToMavenCentralRepository
mavenPublishing {
    publishToMavenCentral(SonatypeHost.CENTRAL_PORTAL)

    signAllPublications()

    coordinates("com.datablit", "kotlin", "1.0.0")

    pom {
        name = "Analytics library"
        description = "datablit analytics library to track events"
        inceptionYear = "2025"
        url = "https://github.com/datablit/datablit-kotlin"
        licenses {
            license {
                name = "The Apache License, Version 2.0"
                url = "https://www.apache.org/licenses/LICENSE-2.0.txt"
                distribution = "https://www.apache.org/licenses/LICENSE-2.0.txt"
            }
        }
        developers {
            developer {
                id = "datablit"
                name = "datablit"
                url = "https://github.com/datablit"
            }
        }
        scm {
            url = "https://github.com/datablit/datablit-kotlin/"
            connection = "scm:git:git://github.com/datablit/datablit-kotlin.git"
            developerConnection = "scm:git:ssh://git@github.com/datablit/datablit-kotlin.git"
        }
    }
}