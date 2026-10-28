import build_src.constants.ApplicationConfig

plugins {
    id(libs.plugins.convention.kmm.library.kotlin.get().pluginId)
}

kotlin {
    android {
        namespace = "${ApplicationConfig.ROOT_PACKAGE}.feature.settings.domain.impl"
    }
    sourceSets {
        commonMain.dependencies {
            implementation(projects.core)
            implementation(projects.core.domain)
            implementation(projects.feature.authorization.domain.api)
            implementation(projects.feature.authorization.data.source)
            implementation(projects.feature.settings.domain.api)
            implementation(projects.feature.settings.data.source)
        }
        commonTest.dependencies {
            implementation(projects.core.testFixtures)
        }
    }
}