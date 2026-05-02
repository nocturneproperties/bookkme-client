plugins {
    id(libs.plugins.convention.kmm.library.kotlin.get().pluginId)
}

android {
    namespace = "library.money.impl"
}

kotlin {
    sourceSets {
        androidMain.dependencies {
            implementation(libs.joda.money)
        }
        commonMain.dependencies {
            implementation(projects.core)
            implementation(projects.core.domain)
            implementation(projects.library.money.api)
        }
    }
}