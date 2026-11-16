import build_src.constants.ApplicationConfig
import build_src.tools.libs
import com.google.firebase.appdistribution.gradle.firebaseAppDistribution
import java.util.Properties

plugins {
    id(libs.plugins.convention.android.application.get().pluginId)
}

val keystoreProperties = Properties().apply {
    val keystorePropertiesFile = file("keystore.properties")
    if (keystorePropertiesFile.exists()) {
        keystorePropertiesFile.inputStream().use { load(it) }
    }
}

android {
    namespace = ApplicationConfig.ROOT_PACKAGE

    ndkVersion = "30.0.14904198"

    defaultConfig {
        applicationId = ApplicationConfig.ROOT_PACKAGE
        versionCode = ApplicationConfig.VERSION_CODE
        versionName = ApplicationConfig.VERSION_NAME
    }

    signingConfigs {
        getByName("debug") {
            keyAlias = keystoreProperties.getProperty("debugKeyAlias")
            keyPassword = keystoreProperties.getProperty("debugKeyPassword")
            storeFile = keystoreProperties.getProperty("debugStoreFile")?.let(::file)
            storePassword = keystoreProperties.getProperty("debugStorePassword")
        }
        create("release") {
            keyAlias = keystoreProperties.getProperty("releaseKeyAlias")
            keyPassword = keystoreProperties.getProperty("releaseKeyPassword")
            storeFile = keystoreProperties.getProperty("releaseStoreFile")?.let(::file)
            storePassword = keystoreProperties.getProperty("releaseStorePassword")
        }
    }

    buildTypes {
        getByName("debug") {
            signingConfig = signingConfigs.getByName("debug")
            firebaseAppDistribution {
                groups = "qa"
            }
        }
        getByName("release") {
            signingConfig = signingConfigs.getByName("release")
            firebaseAppDistribution {
                groups = "qa"
            }
        }
    }
}

dependencies {
    implementation(projects.shared)
    implementation(libs.androidx.splash)

    implementation(libs.koin.core)
    implementation(libs.koin.android)
    implementation(platform(libs.compose.bom))
    implementation(libs.compose.ui)
    debugImplementation(libs.compose.ui.tooling)
    implementation(libs.compose.material3)
    implementation(libs.compose.navigation)
    implementation(libs.androidx.activity.compose)

    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.crashlytics)

    implementation(projects.library.credentials.impl)
}
