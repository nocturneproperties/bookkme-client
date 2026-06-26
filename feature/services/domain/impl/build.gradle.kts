import build_src.constants.ApplicationConfig

plugins {
    id(libs.plugins.convention.kmm.library.kotlin.get().pluginId)
}

kotlin {
    android {
        namespace = "${ApplicationConfig.ROOT_PACKAGE}.feature.services.domain.impl"
    }
}


kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(projects.core)
            implementation(projects.core.domain)
            implementation(projects.feature.services.domain.api)
            implementation(projects.feature.services.data.source)
        }
        commonTest.dependencies {
            implementation(projects.core.testFixtures)
        }
    }
}