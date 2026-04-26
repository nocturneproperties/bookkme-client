plugins {
    id(libs.plugins.convention.kmm.library.kotlin.get().pluginId)
}

android {
    namespace = "library.credentials.impl"
}

kotlin {
    sourceSets {
        androidMain.dependencies {
            implementation(libs.androidx.credentials)
            implementation(libs.androidx.credentials.compat)
        }
        commonMain.dependencies {
            implementation(projects.core)
            implementation(libs.koin.core)
            implementation(projects.library.credentials.api)
        }
    }
}
