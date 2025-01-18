plugins {
    alias(libs.plugins.bookk.kmm.library)
}

android {
    namespace = "me.bookk.domain.environment.impl"
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(projects.core.di)
            implementation(projects.domain.environment.api)
            api(projects.core.domain)
        }
    }
}