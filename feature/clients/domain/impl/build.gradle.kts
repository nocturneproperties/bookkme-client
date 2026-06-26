import build_src.constants.ApplicationConfig

plugins {
    id(libs.plugins.convention.kmm.library.kotlin.get().pluginId)
}

kotlin {
    android {
        namespace = "${ApplicationConfig.ROOT_PACKAGE}.feature.clients.domain.impl"
    }
}


kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(projects.core)
            implementation(projects.core.domain)
            implementation(projects.feature.clients.domain.api)
            implementation(projects.feature.clients.data.source)
        }
        commonTest.dependencies {
            implementation(projects.core.testFixtures)
        }
    }
}