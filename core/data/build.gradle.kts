plugins {
    alias(libs.plugins.convention.kmm.library)
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "me.bookk.core.data"
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            api(libs.ktor.client.core)
            api(libs.ktor.client.mock)
            api(libs.ktor.client.protobuf)
            api(projects.core.domain)
        }
    }
}