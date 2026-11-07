import build_src.constants.ApplicationConfig

plugins {
    id(libs.plugins.convention.kmm.library.kotlin.get().pluginId)
}

kotlin {
    android {
        namespace = "${ApplicationConfig.ROOT_PACKAGE}.library.credentials.impl"
    }
    sourceSets {
        androidMain.dependencies {
            implementation(libs.androidx.credentials)
            implementation(libs.androidx.credentials.compat)
        }
        commonMain.dependencies {
            implementation(projects.core)
            implementation(projects.library.credentials.api)
        }
    }
}
