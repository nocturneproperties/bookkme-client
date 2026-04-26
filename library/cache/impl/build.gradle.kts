plugins {
    id(libs.plugins.convention.kmm.library.kotlin.get().pluginId)
}

android {
    namespace = "library.cache.impl"
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(libs.koin.core)
            implementation(projects.core)
            implementation(projects.core.domain)
            implementation(projects.library.cache.api)
            implementation(projects.library.files.api)
            implementation(libs.androidx.preferences)
            implementation(libs.okio)
        }
    }
}