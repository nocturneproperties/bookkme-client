import build_src.constants.ApplicationConfig

plugins {
    id(libs.plugins.convention.kmm.library.kotlin.get().pluginId)
}

kotlin {
    android {
        namespace = "${ApplicationConfig.ROOT_PACKAGE}.database.account"
    }
    sourceSets {
        commonMain.dependencies {
            implementation(libs.room.runtime)
        }
    }
}
