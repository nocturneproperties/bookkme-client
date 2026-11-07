import build_src.constants.ApplicationConfig

plugins {
    id(libs.plugins.convention.kmm.library.kotlin.get().pluginId)
}

kotlin {
    android {
        namespace = "${ApplicationConfig.ROOT_PACKAGE}.library.validation.impl"
    }
    sourceSets {
        commonMain.dependencies {
            implementation(projects.library.validation.api)
        }
        commonTest.dependencies {
            implementation(projects.core.testFixtures)
        }
    }
}
