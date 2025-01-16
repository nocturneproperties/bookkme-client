plugins {
    alias(libs.plugins.bookk.kmm.library)
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "me.bookk.feature.authorization.data"
}

kotlin {
    sourceSets {
        androidMain.dependencies {
            implementation(libs.androidx.credentials)
            implementation(libs.androidx.credentials.compat)
        }
        commonMain.dependencies {
            implementation(projects.core)
            implementation(projects.core.domain)
            implementation(projects.core.data)
            implementation(projects.feature.authorization.domain.api)
            implementation(projects.feature.authorization.domain.datasource)
            implementation(libs.ktor.client.resources)
        }
    }
}