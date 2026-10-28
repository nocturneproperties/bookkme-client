import build_src.constants.ApplicationConfig

plugins {
    id(libs.plugins.convention.kmm.library.kotlin.get().pluginId)
}

kotlin {
    android {
        namespace = "${ApplicationConfig.ROOT_PACKAGE}.feature.business.domain.impl"
    }
    sourceSets {
        commonMain.dependencies {
            implementation(projects.core)
            implementation(projects.core.domain)
            implementation(projects.feature.business.domain.api)
            implementation(projects.feature.business.data.source)
            implementation(projects.feature.employees.domain.api)
            implementation(projects.library.device.api)
        }
        commonTest.dependencies {
            implementation(projects.core.testFixtures)
        }
    }
}
