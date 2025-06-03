plugins {
    alias(libs.plugins.convention.kmm.library)
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "me.bookk.feature.settings.data"
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(projects.core)
            implementation(projects.core.domain)
            implementation(projects.core.data)
            implementation(projects.feature.settings.domain.api)
            implementation(projects.feature.settings.domain.datasource)
            implementation(projects.library.cache.api)
            implementation(libs.ktor.client.resources)
        }
    }
}