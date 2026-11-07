import build_src.constants.ApplicationConfig

plugins {
    id(libs.plugins.convention.kmm.library.kotlin.get().pluginId)
}

kotlin {
    android {
        namespace = "${ApplicationConfig.ROOT_PACKAGE}.core.test"
    }
    sourceSets {
        commonMain.dependencies {
            implementation(libs.kotlinx.coroutines.test)
            implementation(libs.test.kotlin)
        }
    }
}
