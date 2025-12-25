plugins {
    alias(libs.plugins.convention.kmm.library)
}

android {
    namespace = "me.bookk.domain.environment.api"
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(libs.kotlinx.datetime)
            api(projects.core.domain)
        }
    }
}