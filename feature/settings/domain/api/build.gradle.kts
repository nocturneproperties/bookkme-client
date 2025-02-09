plugins {
    alias(libs.plugins.bookk.kmm.library)
}

android {
    namespace = "me.bookk.feature.settings.domain.api"
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(projects.core)
            implementation(projects.core.domain)
        }
    }
}