plugins {
    alias(libs.plugins.convention.kmm.library)
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