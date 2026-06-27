import build_src.constants.ApplicationConfig

plugins {
    id(libs.plugins.convention.kmm.library.kotlin.get().pluginId)
    alias(libs.plugins.kotlin.serialization)
}

kotlin {
    android {
        namespace = "${ApplicationConfig.ROOT_PACKAGE}.feature.appointments.data"
    }
    sourceSets {
        commonMain.dependencies {
            implementation(projects.core)
            implementation(projects.core.domain)
            implementation(projects.core.data)
            implementation(projects.feature.appointments.domain.api)
            implementation(projects.feature.appointments.data.source)
            implementation(projects.database)
            implementation(projects.library.cache.api)
            implementation(libs.ktor.client.resources)
        }
    }
}
