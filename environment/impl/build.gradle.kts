plugins {
    alias(libs.plugins.convention.kmm.library)
}

android {
    namespace = "me.bookk.domain.environment.impl"
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(projects.environment.api)
            api(projects.core.domain)
        }
    }
}