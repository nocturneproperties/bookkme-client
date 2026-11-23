import build_src.constants.ApplicationConfig

plugins {
    id(libs.plugins.convention.kmm.library.kotlin.get().pluginId)
}

kotlin {
    android {
        namespace = "${ApplicationConfig.ROOT_PACKAGE}.feature.booking.data.source"
    }
    sourceSets {
        commonMain.dependencies {
            implementation(projects.feature.booking.domain.api)
        }
    }
}
