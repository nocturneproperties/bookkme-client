plugins {
    alias(libs.plugins.bookk.kmm.library)
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "me.bookk.feature.business.data"
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(projects.core)
            implementation(projects.core.domain)
            implementation(projects.core.data)
            implementation(projects.feature.business.domain.api)
            implementation(projects.feature.business.domain.datasource)
            implementation(projects.database)
            implementation(libs.ktor.client.resources)
        }
    }
}