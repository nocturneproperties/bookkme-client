plugins {
    id(libs.plugins.convention.kmm.library.kotlin.get().pluginId)
    alias(libs.plugins.kotlin.serialization)
}

kotlin {
    android {
        namespace = "me.bookk.feature.settings.data"
    }
    sourceSets {
        commonMain.dependencies {
            implementation(projects.core)
            implementation(projects.core.domain)
            implementation(projects.core.data)
            implementation(projects.feature.settings.domain.api)
            implementation(projects.feature.settings.data.source)
            implementation(projects.library.cache.api)
            implementation(libs.ktor.client.resources)
        }
    }
}
