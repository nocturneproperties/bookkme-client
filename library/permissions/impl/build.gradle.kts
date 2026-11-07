import build_src.constants.ApplicationConfig

plugins {
    id(libs.plugins.convention.kmm.library.kotlin.get().pluginId)
}

kotlin {
    android {
        namespace = "${ApplicationConfig.ROOT_PACKAGE}.library.permissions.impl"
    }
    sourceSets {
        androidMain.dependencies {
            implementation(libs.androidx.activity)
        }
        commonMain.dependencies {
            implementation(projects.core)
            implementation(projects.library.permissions.api)
        }
    }
}
