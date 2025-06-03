plugins {
    alias(libs.plugins.convention.kmm.library)
}

android {
    namespace = "library.files.impl"
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(projects.core)
            implementation(projects.core.domain)
            implementation(projects.library.files.api)
        }
    }
}