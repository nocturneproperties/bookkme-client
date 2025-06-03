plugins {
    alias(libs.plugins.convention.kmm.library)
}

android {
    namespace = "me.bookk.feature.settings.domain.datasource"
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(projects.core)
            implementation(projects.core.domain)
            implementation(projects.feature.settings.domain.api)
        }
    }
}