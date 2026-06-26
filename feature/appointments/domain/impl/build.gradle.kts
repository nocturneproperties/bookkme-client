import build_src.constants.ApplicationConfig

plugins {
    id(libs.plugins.convention.kmm.library.kotlin.get().pluginId)
}

kotlin {
    android {
        namespace = "${ApplicationConfig.ROOT_PACKAGE}.feature.appointments.domain.impl"
    }
    sourceSets {
        commonMain.dependencies {
            implementation(projects.core)
            implementation(projects.core.domain)
            implementation(projects.feature.appointments.domain.api)
            implementation(projects.feature.appointments.data.source)
            implementation(projects.feature.business.domain.api)
            implementation(projects.feature.clients.domain.api)
            implementation(projects.feature.services.domain.api)
            implementation(projects.feature.authorization.domain.api)
        }
        commonTest.dependencies {
            implementation(projects.core.testFixtures)
        }
    }
}
