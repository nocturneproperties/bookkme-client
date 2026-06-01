plugins {
    id(libs.plugins.convention.kmm.library.kotlin.get().pluginId)
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "me.bookk.core.domain"
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            api(projects.core)
            implementation(libs.kotlin.serialization.core)
        }
    }
}