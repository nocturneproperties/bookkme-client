import build_src.tools.libs

plugins {
    alias(libs.plugins.kotlin.serialization)
    id(libs.plugins.convention.kmm.library.kotlin.get().pluginId)
}

kotlin {
    android {
        namespace = "library.money"
    }
    sourceSets {
        androidMain.dependencies {
            implementation(libs.joda.money)
        }
        commonMain.dependencies {
            implementation(projects.core)
            implementation(projects.core.domain)
            implementation(libs.kotlin.serialization.core)
        }
    }
}