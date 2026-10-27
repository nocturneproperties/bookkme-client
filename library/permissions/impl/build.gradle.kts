plugins {
    id(libs.plugins.convention.kmm.library.kotlin.get().pluginId)
}

kotlin {
    android {
        namespace = "library.permissions.impl"
    }
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
