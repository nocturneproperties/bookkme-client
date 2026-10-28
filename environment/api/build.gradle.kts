import build_src.constants.ApplicationConfig

plugins {
    id(libs.plugins.convention.kmm.library.kotlin.get().pluginId)
}

kotlin {
    android {
        namespace = "${ApplicationConfig.ROOT_PACKAGE}.domain.environment.api"
    }
    sourceSets {
        commonMain.dependencies {
            implementation(libs.kotlinx.datetime)
            api(projects.core.domain)
        }
    }
}