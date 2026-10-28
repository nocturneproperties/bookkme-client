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
            implementation(libs.androidx.lifecycle.viewmodel)
            implementation(libs.androidx.activity)
        }
        commonMain.dependencies {
            implementation(projects.core)
            implementation(libs.koin.core)
            implementation(projects.library.credentials.api)
        }
    }
}
