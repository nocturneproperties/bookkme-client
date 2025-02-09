plugins {
    alias(libs.plugins.bookk.kmm.library)
}

android {
    namespace = "me.bookk.feature.settings.data"
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(projects.core)
            implementation(projects.core.domain)
            implementation(projects.core.data)
            implementation(projects.feature.platform.domain.datasource)
            implementation(projects.feature.settings.domain.api)
            implementation(projects.feature.settings.domain.datasource)
        }
    }
}