plugins {
    id(libs.plugins.convention.kmm.library.kotlin.get().pluginId)
}

kotlin {
    android {
        namespace = "me.bookk.feature.settings.domain.impl"
    }
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(projects.core)
            implementation(projects.core.domain)
            implementation(projects.feature.authorization.domain.api)
            implementation(projects.feature.authorization.data.source)
            implementation(projects.feature.settings.domain.api)
            implementation(projects.feature.settings.domain.datasource)
        }
        commonTest.dependencies {
            implementation(projects.core.testFixtures)
        }
    }
}