plugins {
    alias(libs.plugins.bookk.kmm.library)
}

android {
    namespace = "me.bookk.feature.platform.data"
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(projects.core)
            implementation(projects.core.domain)
            implementation(projects.core.data)
            implementation(projects.feature.platform.domain.api)
            implementation(projects.feature.platform.domain.datasource)
            implementation(libs.androidx.preferences)
            implementation(libs.okio)
        }
    }
}