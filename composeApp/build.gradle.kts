import com.codingfeline.buildkonfig.compiler.FieldSpec
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import java.util.Properties

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidMultiplatformLibrary)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.kotlinxSerialization)
    alias(libs.plugins.buildkonfig)
}

kotlin {
    listOf(
        iosArm64(),
        iosSimulatorArm64()
    ).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "ComposeApp"
            isStatic = true
        }
    }

    android {
        namespace = "com.juanpablo0612.carpool"
        compileSdk = libs.versions.android.compileSdk.get().toInt()
        minSdk = libs.versions.android.minSdk.get().toInt()
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_17)
        }
        androidResources {
            enable = true
        }
    }

    sourceSets {
        iosMain.dependencies {
            implementation(libs.ktor.client.darwin)
            implementation(libs.compass.geolocation.mobile)
            implementation(libs.compass.permissions.mobile)
        }
        androidMain.dependencies {
            implementation(project.dependencies.platform(libs.firebase.bom))
            implementation(libs.google.maps.compose)
            implementation(libs.compass.geolocation.mobile)
            implementation(libs.compass.permissions.mobile)
            implementation(libs.ktor.client.okhttp)
        }
        commonMain.dependencies {
            implementation(libs.compass.geolocation)
            implementation(libs.compose.runtime)
            implementation(libs.compose.foundation)
            implementation(libs.compose.material3)
            implementation(libs.compose.ui)
            implementation(libs.compose.ui.backhandler)
            implementation(libs.compose.components.resources)
            implementation(libs.compose.uiToolingPreview)
            implementation(libs.androidx.lifecycle.viewmodelCompose)
            implementation(libs.androidx.lifecycle.runtimeCompose)
            implementation(project.dependencies.platform(libs.koin.bom))
            implementation(libs.koin.core)
            implementation(libs.koin.compose)
            implementation(libs.koin.compose.viewmodel)
            implementation(libs.navigation.compose)
            implementation(libs.firebase.analytics)
            implementation(libs.firebase.auth)
            implementation(libs.firebase.firestore)
            implementation(libs.firebase.storage)
            implementation(libs.kotlinx.serialization.json)
            implementation(libs.kotlinx.datetime)
            implementation(libs.filekit.core)
            implementation(libs.filekit.dialogs.compose)
            implementation(libs.filekit.coil)
            implementation(libs.coil.compose)
            implementation(libs.coil.network.ktor3)
            implementation(libs.ktor.client.core)
            implementation(libs.ktor.client.content.negotiation)
            implementation(libs.ktor.serialization.kotlinx.json)
            implementation(libs.datastore.preferences.core)
        }
        commonTest.dependencies {
            implementation(libs.kotlin.test)
        }
    }
}

dependencies {
    androidRuntimeClasspath(libs.compose.uiTooling)
}

buildkonfig {
    packageName = "com.juanpablo0612.carpool.core.config"

    defaultConfigs {
        val secretsProperties = Properties()
        val secretsPropertiesFile = rootProject.file("secrets.properties")
        if (secretsPropertiesFile.exists()) {
            secretsProperties.load(secretsPropertiesFile.inputStream())
        }
        buildConfigField(
            FieldSpec.Type.STRING,
            "MAPS_API_KEY",
            secretsProperties.getProperty("MAPS_API_KEY") ?: "",
        )
    }
}