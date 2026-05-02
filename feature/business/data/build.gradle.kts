plugins {
    id(libs.plugins.convention.kmm.library.kotlin.get().pluginId)
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "me.bookk.feature.business.data"
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(projects.core)
            implementation(projects.core.domain)
            implementation(projects.core.data)
            implementation(projects.feature.business.domain.api)
            implementation(projects.feature.business.data.source)
            implementation(projects.database)
            implementation(projects.library.cache.api)
            implementation(libs.ktor.client.resources)
        }
    }
}