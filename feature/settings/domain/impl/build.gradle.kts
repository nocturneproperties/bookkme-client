plugins {
    alias(libs.plugins.bookk.kmm.library)
}

android {
    namespace = "me.bookk.feature.settings.domain.impl"
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(projects.core)
            implementation(projects.core.domain)
            implementation(projects.feature.authorization.domain.api)
            implementation(projects.feature.authorization.domain.datasource)
            implementation(projects.feature.settings.domain.api)
            implementation(projects.feature.settings.domain.datasource)
        }
    }
}