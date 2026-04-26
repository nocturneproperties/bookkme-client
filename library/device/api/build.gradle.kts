plugins {
    id(libs.plugins.convention.kmm.library.kotlin.get().pluginId)
}

android {
    namespace = "library.device.api"
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(projects.core)
            implementation(projects.core.domain)
        }
    }
}