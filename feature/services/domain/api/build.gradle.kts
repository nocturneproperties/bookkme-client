import build_src.constants.ApplicationConfig

plugins {
    id(libs.plugins.convention.kmm.library.kotlin.get().pluginId)
}

android {
    namespace = "${ApplicationConfig.ROOT_PACKAGE}.feature.services.domain.api"
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(projects.core)
            implementation(projects.core.domain)
            api(projects.library.money.api)
        }
    }
}