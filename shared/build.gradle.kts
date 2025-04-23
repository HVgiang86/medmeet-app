import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlinx.serialization)
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.buildConfig)
    alias(libs.plugins.ktlint)
}

buildscript {
    apply(from = "$rootDir/team-props/git-hooks.gradle.kts")
}

kotlin {
    androidTarget {
        @OptIn(ExperimentalKotlinGradlePluginApi::class)
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_17)
        }
    }
    listOf(
        iosX64(),
        iosArm64(),
        iosSimulatorArm64()
    ).forEach {
        it.binaries.framework {
            baseName = "RssReader"
        }
    }

    sourceSets {
        commonMain.dependencies {
            // Network
//            implementation(libs.ktor.core)
//            implementation(libs.ktor.logging)
            implementation(libs.bundles.ktor)
            // Coroutines
            implementation(libs.kotlinx.coroutines.core)
            // Logger
            implementation(libs.napier)
            // JSON
            implementation(libs.kotlinx.serialization.json)
            // Key-Value storage
            implementation(libs.multiplatform.settings)
            implementation(libs.multiplatform.settings.noargs)
            // DI
            api(libs.koin.core)

            implementation(libs.datastore.preferences)
            implementation(libs.datastore)

            // Time
            implementation(libs.kotlinx.datetime)
        }

        androidMain.dependencies {
            // Network
            implementation(libs.ktor.client.okhttp)
        }

        iosMain.dependencies {
            // Network
            implementation(libs.ktor.client.ios)
        }
    }
}

android {
    namespace = "com.huongmt.medmeet.shared"
    sourceSets["main"].manifest.srcFile("src/androidMain/AndroidManifest.xml")
    compileSdk = (findProperty("android.compileSdk") as String).toInt()

    defaultConfig {
        minSdk = (findProperty("android.minSdk") as String).toInt()
    }
    compileOptions {
        // Flag to enable support for the new language APIs
        isCoreLibraryDesugaringEnabled = true
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    dependencies {
        coreLibraryDesugaring(libs.desugar.jdk.libs)
    }
}

ktlint {
    disabledRules.set(
        setOf(
            "no-wildcard-imports",
            "standard_no-wildcard-imports",
            "standard_filename",
            "standard_function-naming"
        )
    )
}
