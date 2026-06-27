plugins {
    id(libs.plugins.convention.kmm.library.kotlin.get().pluginId)
}

kotlin {
    android {
        namespace = "me.bookk.domain.environment.impl"
    }
    sourceSets {
        commonMain.dependencies {
            implementation(projects.environment.api)
            api(projects.core.domain)
        }
    }
}