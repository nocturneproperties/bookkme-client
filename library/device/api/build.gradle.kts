plugins {
    alias(libs.plugins.convention.kmm.library)
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