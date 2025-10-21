import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.compose.multiplatform)
    alias(libs.plugins.compose.compiler)

    alias(libs.plugins.jetbrains.kotlin.serialization)

    alias(libs.plugins.aboutLibraries)
}

aboutLibraries {
    export {
        outputFile = file("src/commonMain/composeResources/files/aboutlibraries.json")
        prettyPrint = true
    }
    library {
        // Enable the duplication mode, allows to merge, or link dependencies which relate
        duplicationMode = com.mikepenz.aboutlibraries.plugin.DuplicateMode.MERGE
    }
}

kotlin {
    androidTarget {
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_17)
        }
    }

    listOf(
        iosArm64(),
        iosSimulatorArm64()
    ).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "ComposeApp"
            isStatic = true
        }
    }

    jvm("desktop") {
        compilations.all {
            compileTaskProvider.configure {
                compilerOptions {
                    jvmTarget.set(JvmTarget.JVM_17)
                }
            }
        }
    }

    sourceSets {
        val desktopMain by getting

        androidMain.dependencies {
            implementation(projects.dataStore)

            implementation(compose.preview)
            implementation(libs.androidx.activity.compose)

            implementation(libs.koin.android)
            implementation(libs.koin.androidx.compose)
            implementation(libs.ktor.client.okhttp)
        }
        commonMain.dependencies {
            implementation(projects.shared)
            implementation(projects.dataStore)
            implementation(projects.legal)

            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.material3)
            implementation(compose.materialIconsExtended) // More Icons
            implementation(compose.ui)
            implementation(compose.components.resources)
            implementation(compose.components.uiToolingPreview)
            implementation(libs.androidx.lifecycle.viewmodelCompose)
            implementation(libs.androidx.lifecycle.runtimeCompose)

            // Navigation
            implementation(libs.jetbrains.compose.navigation)
            implementation(libs.kotlinx.serialization.json)

            // Koin (DI)
            api(libs.koin.core)
            implementation(libs.koin.compose)
            implementation(libs.koin.compose.viewmodel)

            // Ktor (Networking)
            implementation(libs.bundles.ktor)

            // Datetime
            implementation(libs.kotlinx.datetime)

            // Coil (Image Loading)
            implementation(libs.bundles.coil)

            // QR-Code Scanner (QR-Kit)
            implementation(libs.qr.kit)

            // Back Handler
            implementation(libs.ui.backhandler)

            // About Libraries
            implementation(libs.aboutlibraries.compose.core)
            implementation(libs.aboutlibraries.compose.m3)
        }
        iosMain.dependencies {
            implementation(projects.dataStore)

            implementation(libs.ktor.client.darwin)
        }
        desktopMain.dependencies {
            implementation(projects.dataStore)

//            implementation(libs.kotlin.test)
//            implementation(compose.desktop.currentOs)
//            implementation(libs.kotlinx.coroutines.swing)
//            implementation(libs.kotlin.stdlib)
//            implementation(libs.koin.compose)
//            implementation(libs.koin.compose.viewmodel)

            implementation(libs.ktor.client.okhttp)
        }
    }
}

android {
    namespace = "de.malteans.sosactivities"
    compileSdk = libs.versions.android.compileSdk.get().toInt()

    defaultConfig {
        applicationId = libs.versions.projectApplicationId.get()
        minSdk = libs.versions.android.minSdk.get().toInt()
        targetSdk = libs.versions.android.targetSdk.get().toInt()
        versionName = libs.versions.projectVersionName.get()
        versionNameSuffix = libs.versions.projectVersionNameSuffix.get()
        versionCode = libs.versions.projectVersionCode.get().toInt()
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}

dependencies {
    debugImplementation(compose.uiTooling)
}

compose.desktop {
    application {
        mainClass = "de.malteans.sosactivities.MainKt"

        nativeDistributions {
            packageName = libs.versions.desktop.packageName.get()
            packageVersion = libs.versions.projectVersionName.get()

            windows {
                iconFile.set(project.file("src/desktopMain/resources/ic_launcher.ico"))
            }

            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
        }
    }
}