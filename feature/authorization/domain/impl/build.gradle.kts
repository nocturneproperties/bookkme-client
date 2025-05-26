plugins {
    alias(libs.plugins.bookk.kmm.library)
}

android {
    namespace = "me.bookk.feature.authorization.domain.impl"
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(projects.core)
            implementation(projects.core.domain)
            implementation(projects.feature.authorization.domain.api)
            implementation(projects.feature.business.domain.api)
            implementation(projects.feature.platform.domain.api)
            implementation(projects.feature.authorization.domain.datasource)
        }
    }
}