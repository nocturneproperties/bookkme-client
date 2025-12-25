plugins {
    alias(libs.plugins.convention.kmm.library)
}

android {
    namespace = "library.device.impl"
}

kotlin {
    sourceSets {
        androidMain.dependencies {
            implementation(libs.androidx.core)
        }
        commonMain.dependencies {
            implementation(projects.core)
            implementation(projects.core.domain)
            implementation(projects.library.device.api)
        }
    }
}