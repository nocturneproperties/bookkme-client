import build_src.constants.ApplicationConfig
import java.util.Properties

plugins {
    alias(libs.plugins.convention.android.application)
}

val keystoreProperties = file("keystore.properties").inputStream().use {
    Properties().apply {
        load(it)
    }
}

android {
    namespace = ApplicationConfig.ROOT_PACKAGE

    defaultConfig {
        applicationId = ApplicationConfig.ROOT_PACKAGE
        versionCode = ApplicationConfig.VERSION_CODE
        versionName = ApplicationConfig.VERSION_NAME
    }

    signingConfigs {
        getByName("debug") {
            keyAlias = keystoreProperties["debugKeyAlias"] as String
            keyPassword = keystoreProperties["debugKeyPassword"] as String
            storeFile = file(keystoreProperties["debugStoreFile"] as String)
            storePassword = keystoreProperties["debugStorePassword"] as String
        }
        create("release") {
            keyAlias = keystoreProperties["releaseKeyAlias"] as String
            keyPassword = keystoreProperties["releaseKeyPassword"] as String
            storeFile = file(keystoreProperties["releaseStoreFile"] as String)
            storePassword = keystoreProperties["releaseStorePassword"] as String
        }
    }

    buildTypes {
        getByName("debug") {
            signingConfig = signingConfigs.getByName("debug")
        }
        getByName("release") {
            signingConfig = signingConfigs.getByName("release")
        }
    }
}

dependencies {
    implementation(projects.shared)
}