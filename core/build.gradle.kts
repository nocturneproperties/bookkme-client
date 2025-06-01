plugins {
    alias(libs.plugins.convention.kmm.library)
}

android {
    namespace = "me.bookk.core"
}

kotlin {
    sourceSets {
        androidMain.dependencies {
            implementation(libs.androidx.core)
            implementation(libs.androidx.activity)
        }
    }
}