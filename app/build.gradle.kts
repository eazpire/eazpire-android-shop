import java.util.Properties

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}

val keystorePropertiesFile = rootProject.file("keystore.properties")
val keystoreProperties = Properties()
if (keystorePropertiesFile.exists()) {
    keystorePropertiesFile.inputStream().use { keystoreProperties.load(it) }
}

android {
    namespace = "com.eazpire.shop"
    compileSdk = 36
    if (System.getenv("CI") != "true") {
        buildDir = file("${System.getProperty("java.io.tmpdir")}/eazpire-android-shop-build/${project.name}")
    }

    defaultConfig {
        applicationId = "com.eazpire.shop"
        minSdk = 26
        targetSdk = 36
        val appVersionCode = (System.getenv("VERSION_CODE") ?: "1").toIntOrNull() ?: 1
        versionCode = appVersionCode
        versionName = System.getenv("VERSION_NAME") ?: "0.1.0 ($appVersionCode)"
    }

    signingConfigs {
        if (keystorePropertiesFile.exists()) {
            create("release") {
                keyAlias = keystoreProperties.getProperty("keyAlias")
                keyPassword = keystoreProperties.getProperty("keyPassword")
                storeFile = rootProject.file(keystoreProperties.getProperty("storeFile")!!)
                storePassword = keystoreProperties.getProperty("storePassword")
            }
        }
    }
    buildTypes {
        debug {
            buildConfigField("boolean", "USE_EXTERNAL_APP_SWITCH", "true")
        }
        release {
            isMinifyEnabled = false
            buildConfigField("boolean", "USE_EXTERNAL_APP_SWITCH", "true")
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro",
            )
            if (keystorePropertiesFile.exists()) {
                signingConfig = signingConfigs.getByName("release")
            }
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
    buildFeatures {
        buildConfig = true
    }
}

dependencies {
    implementation(project(":android-shared"))

    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.activity:activity-ktx:1.8.1")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("androidx.browser:browser:1.8.0")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.7.0")
    implementation("com.google.android.material:material:1.11.0")
    implementation("androidx.security:security-crypto:1.1.0-alpha06")
    implementation("com.squareup.okhttp3:okhttp:4.12.0")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")

    testImplementation("junit:junit:4.13.2")
}
