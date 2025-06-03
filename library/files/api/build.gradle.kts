plugins {
    alias(libs.plugins.convention.kmm.library)
}

android {
    namespace = "library.files.api"
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(projects.core)
            implementation(projects.core.domain)
        }
    }
}