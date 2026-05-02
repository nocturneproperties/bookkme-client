import build_src.tools.libs

plugins {
    alias(libs.plugins.kotlin.serialization)
    id(libs.plugins.convention.kmm.library.kotlin.get().pluginId)
}

android {
    namespace = "library.money.api"
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(projects.core)
            implementation(projects.core.domain)
            implementation(libs.kotlin.serialization.core)
        }
    }
}