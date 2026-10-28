import build_src.constants.ApplicationConfig

plugins {
    id(libs.plugins.convention.kmm.library.kotlin.get().pluginId)
}

kotlin {
    android {
        namespace = "${ApplicationConfig.ROOT_PACKAGE}.domain.environment.impl"
    }
    sourceSets {
        commonMain.dependencies {
            implementation(projects.environment.api)
            api(projects.core.domain)
        }
    }
}