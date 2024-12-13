plugins {
    alias(libs.plugins.bookk.kmm.library)
}

android {
    namespace = "me.bookk.core.domain"
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            api(projects.core)
        }
    }
}