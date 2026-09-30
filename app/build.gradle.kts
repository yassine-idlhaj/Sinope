import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.ksp)
    alias(libs.plugins.hilt)
    alias(libs.plugins.kotlin.serialization)
}

/**
 * Release signing is configured from keystore.properties, which is gitignored and never leaves
 * this machine. When it is absent — a fresh clone, CI, someone else's checkout — the release
 * build simply goes unsigned instead of failing, so the project still builds for everyone.
 *
 * See keystore.properties.example for the expected keys.
 */
val keystorePropertiesFile = rootProject.file("keystore.properties")
val keystoreProperties = Properties().apply {
    if (keystorePropertiesFile.exists()) {
        keystorePropertiesFile.inputStream().use(::load)
    }
}

android {
    namespace = "com.example.sinope"
    compileSdk {
        version = release(37) {
            minorApiLevel = 1
        }
    }

    defaultConfig {
        applicationId = "io.github.yassineidlhaj.sinope"
        minSdk = 30
        targetSdk = 37
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    signingConfigs {
        if (keystorePropertiesFile.exists()) {
            create("release") {
                storeFile = rootProject.file(keystoreProperties.getProperty("storeFile"))
                storePassword = keystoreProperties.getProperty("storePassword")
                keyAlias = keystoreProperties.getProperty("keyAlias")
                keyPassword = keystoreProperties.getProperty("keyPassword")
            }
        }
    }

    buildTypes {
        debug {
            // Distinct applicationId so a debug build installs alongside the release one
            // instead of replacing it — and so the two keep separate data.
            applicationIdSuffix = ".debug"
            versionNameSuffix = "-debug"
        }

        release {
            // Null when keystore.properties is missing, which leaves the build unsigned.
            signingConfig = signingConfigs.findByName("release")

            // R8: strip unused code and resources. Cuts the dex files by an order of magnitude.
            optimization {
                enable = true
            }

            // Keep rules for libraries that resolve classes reflectively and so are
            // invisible to R8's reachability analysis. See proguard-rules.pro.
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro",
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    buildFeatures {
        compose = true
    }
    testOptions {
        // Robolectric needs the merged resources/manifest to inflate the app's theme on the JVM.
        unitTests.isIncludeAndroidResources = true
    }
}

dependencies {

    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.compose.foundation)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.navigation.common.ktx)
    testImplementation(libs.junit)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.hilt.navigation.compose.v120)


    // Local JVM tests: coroutine control + Robolectric so the Compose onboarding tests run
    // without an emulator.
    testImplementation(libs.androidx.compose.ui.test.junit4)
    testImplementation(libs.kotlinx.coroutines.test)
    testImplementation(libs.robolectric)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.androidx.junit)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
    debugImplementation(libs.androidx.compose.ui.tooling)
    implementation(libs.androidx.compose.material.icons.extended)
    implementation(libs.androidx.core.splashscreen)
    //Datastore
    implementation(libs.androidx.datastore.preferences)
    //Dagger Hilt
    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)

    // Live QR scanning: CameraX preview/analysis + on-device ML Kit barcode decoding.
    implementation(libs.androidx.lifecycle.runtime.compose)
    implementation(libs.androidx.camera.core)
    implementation(libs.androidx.camera.camera2)
    implementation(libs.androidx.camera.lifecycle)
    implementation(libs.androidx.camera.view)
    implementation(libs.mlkit.barcode.scanning)

    /// Room DB
    implementation(libs.androidx.room3.runtime)
    ksp(libs.androidx.room3.compiler)

    /// Codec
    implementation(libs.commons.codec)

    implementation(libs.kotlin.onetimepassword)

    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.biometric)

    implementation(libs.kotlinx.serialization.json)
    // Argon2id password hashing for .sinope backups (lightweight API only, not registered as a JCA provider)
    implementation(libs.bouncycastle.bcprov)


}