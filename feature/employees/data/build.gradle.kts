import build_src.constants.ApplicationConfig

plugins {
    id(libs.plugins.convention.kmm.library.kotlin.get().pluginId)
    alias(libs.plugins.kotlin.serialization)
}

kotlin {
    android {
        namespace = "${ApplicationConfig.ROOT_PACKAGE}.feature.employees.data"
    }
    sourceSets {
        commonMain.dependencies {
            implementation(projects.core)
            implementation(projects.core.domain)
            implementation(projects.core.data)
            implementation(projects.feature.employees.domain.api)
            implementation(projects.feature.employees.data.source)
            implementation(projects.library.cache.api)
            implementation(libs.ktor.client.resources)
        }
        commonTest.dependencies {
            implementation(projects.core.testFixtures)
            implementation(libs.kotlin.serialization.core)
        }
    }
}
