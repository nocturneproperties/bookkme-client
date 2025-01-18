plugins {
    alias(libs.plugins.bookk.kmm.library)
}

android {
    namespace = "me.bookk.feature.platform.domain.datasource"
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(projects.core)
            implementation(projects.core.domain)
            implementation(projects.feature.platform.domain.api)
        }
    }
}