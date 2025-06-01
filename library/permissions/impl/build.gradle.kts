plugins {
    alias(libs.plugins.convention.kmm.library)
}

android {
    namespace = "library.permissions.impl"
}

kotlin {
    sourceSets {
        androidMain.dependencies {
            implementation(libs.androidx.activity)
        }
        commonMain.dependencies {
            implementation(libs.koin.core)
            implementation(projects.core)
            implementation(projects.core.domain)
            implementation(projects.library.permissions.api)
        }
    }
}
