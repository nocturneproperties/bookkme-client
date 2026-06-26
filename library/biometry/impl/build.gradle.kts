plugins {
    id(libs.plugins.convention.kmm.library.kotlin.get().pluginId)
}

kotlin {
    android {
        namespace = "library.biometry.impl"
    }
}

kotlin {
    sourceSets {
        androidMain.dependencies {
            implementation(libs.androidx.biometry)
            implementation(libs.androidx.activity)
        }
        commonMain.dependencies {
            implementation(libs.koin.core)
            implementation(libs.kmm.resources)
            implementation(projects.core)
            implementation(projects.core.domain)
            implementation(projects.library.biometry.api)
            implementation(projects.library.cache.api)
        }
    }
}