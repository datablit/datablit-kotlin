import com.vanniktech.maven.publish.SonatypeHost

plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    `maven-publish`
    id("com.vanniktech.maven.publish") version "0.32.0"
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

//publishing {
//    publications {
//        create<MavenPublication>("release") {
//            afterEvaluate { // Ensures Gradle configures the component properly
//                from(components["release"])
//            }
//
//            groupId = "com.datablit.analytics"
//            artifactId = "kotlin"
//            version = "1.0.0"
//        }
//    }
//}

//publishing {
//    publications {
//        create<MavenPublication>("mavenJava") {
//            pom {
//                name = "analytics"
//                description = "kotlin analytics library to track events"
//                url = "https://github.com/datablit/analytics-kotlin"
////                properties = mapOf(
////                    "myProp" to "value",
////                    "prop.with.dots" to "anotherValue"
////                )
//                licenses {
//                    license {
//                        name = "The Apache License, Version 2.0"
//                        url = "http://www.apache.org/licenses/LICENSE-2.0.txt"
//                    }
//                }
//                developers {
//                    developer {
//                        id = "datablit"
//                        name = "datablit"
//                        email = "admin@datablit.com"
//                    }
//                }
//                scm {
//                    connection = "scm:git:git://example.com/my-library.git"
//                    developerConnection = "scm:git:ssh://example.com/my-library.git"
//                    url = "https://github.com/datablit/analytics-kotlin"
//                }
//            }
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

// <module directory>/build.gradle.kts

mavenPublishing {
    publishToMavenCentral(SonatypeHost.CENTRAL_PORTAL)

    signAllPublications()

    coordinates(group.toString(), "analytics", version.toString())

    pom {
        name = "Analytics library"
        description = "datablit analytics library to track events"
        inceptionYear = "2025"
        url = "https://github.com/datablit/analytics-kotlin"
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
            url = "https://github.com/datablit/analytics-kotlin/"
            connection = "scm:git:git://github.com/datablit/analytics-kotlin.git"
            developerConnection = "scm:git:ssh://git@github.com/datablit/analytics-kotlin.git"
        }
    }
}