plugins {
    id(libs.plugins.convention.kmm.library.kotlin.get().pluginId)
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