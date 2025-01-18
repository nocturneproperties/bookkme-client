plugins {
    alias(libs.plugins.bookk.kmm.library)
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "me.bookk.core.data"
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(libs.ktor.client.core)
            implementation(projects.core.domain)
        }
    }
}