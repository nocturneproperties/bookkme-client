plugins {
    alias(libs.plugins.bookk.kmm.library.compose)
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