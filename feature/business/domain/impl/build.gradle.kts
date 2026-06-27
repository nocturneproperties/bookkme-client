plugins {
    id(libs.plugins.convention.kmm.library.kotlin.get().pluginId)
}

kotlin {
    android {
        namespace = "me.bookk.feature.business.domain.impl"
    }
    sourceSets {
        commonMain.dependencies {
            implementation(projects.core)
            implementation(projects.core.domain)
            implementation(projects.feature.business.domain.api)
            implementation(projects.feature.business.data.source)
        }
        commonTest.dependencies {
            implementation(projects.core.testFixtures)
        }
    }
}
