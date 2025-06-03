plugins {
    alias(libs.plugins.convention.kmm.library)
}

android {
    namespace = "me.bookk.feature.business.data.source"
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(projects.core)
            implementation(projects.core.domain)
            implementation(projects.feature.business.domain.api)
        }
    }
}