import build_src.constants.ApplicationConfig

plugins {
    id(libs.plugins.convention.kmm.library.kotlin.get().pluginId)
}

kotlin {
    android {
        namespace = "${ApplicationConfig.ROOT_PACKAGE}.feature.booking.data"
    }
    sourceSets {
        commonMain.dependencies {
            implementation(projects.core.domain)
            implementation(projects.feature.booking.domain.api)
            implementation(projects.feature.booking.data.source)
        }
        commonTest.dependencies {
            implementation(projects.core.testFixtures)
        }
    }
}
