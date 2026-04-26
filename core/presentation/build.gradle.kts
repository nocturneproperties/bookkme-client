plugins {
    id(libs.plugins.convention.kmm.library.compose.get().pluginId)
}

android {
    namespace = "me.bookk.core.presentation"
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(projects.core)
        }
    }
}