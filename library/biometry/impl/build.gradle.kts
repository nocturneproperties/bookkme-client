import build_src.constants.ApplicationConfig

plugins {
    id(libs.plugins.convention.kmm.library.kotlin.get().pluginId)
}

kotlin {
    android {
        namespace = "${ApplicationConfig.ROOT_PACKAGE}.library.biometry.impl"
    }
    sourceSets {
        androidMain.dependencies {
            implementation(libs.androidx.biometry)
            implementation(libs.androidx.activity)
        }
        commonMain.dependencies {
            implementation(libs.koin.core)
            implementation(libs.kmm.resources)
            implementation(projects.core)
            implementation(projects.core.domain)
            implementation(projects.library.biometry.api)
            implementation(projects.library.cache.api)
        }
    }
}
