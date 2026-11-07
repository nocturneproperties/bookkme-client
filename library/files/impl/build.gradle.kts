import build_src.constants.ApplicationConfig

plugins {
    id(libs.plugins.convention.kmm.library.kotlin.get().pluginId)
}

kotlin {
    android {
        namespace = "${ApplicationConfig.ROOT_PACKAGE}.library.files.impl"
    }
    sourceSets {
        commonTest.dependencies {
            implementation(projects.core.testFixtures)
        }
        commonMain.dependencies {
            implementation(projects.library.files.api)
        }
    }
}
