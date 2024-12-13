plugins {
    alias(libs.plugins.bookk.kmm.library)
}

android {
    namespace = "me.bookk.core"
}

kotlin {
    sourceSets {
        commonMain.dependencies {
        }
    }
}