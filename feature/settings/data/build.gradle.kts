import build_src.constants.ApplicationConfig

plugins {
    id(libs.plugins.convention.kmm.library.kotlin.get().pluginId)
    alias(libs.plugins.kotlin.serialization)
}

kotlin {
    android {
        namespace = "${ApplicationConfig.ROOT_PACKAGE}.feature.settings.data"
    }
    sourceSets {
        commonTest.dependencies {
            implementation(libs.ktor.client.negotiation)
            implementation(projects.core.testFixtures)
            implementation(libs.kotlin.serialization.core)
        }
        commonMain.dependencies {
            implementation(projects.core)
            implementation(projects.core.domain)
            implementation(projects.core.data)
            implementation(projects.feature.settings.domain.api)
            implementation(projects.feature.settings.data.source)
            implementation(projects.database)
            implementation(projects.library.cache.api)
            implementation(libs.ktor.client.resources)
        }
    }
}
