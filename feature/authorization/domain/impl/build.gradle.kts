plugins {
    id(libs.plugins.convention.kmm.library.kotlin.get().pluginId)
}

kotlin {
    android {
        namespace = "me.bookk.feature.authorization.domain.impl"
    }
    sourceSets {
        commonMain.dependencies {
            implementation(projects.core)
            implementation(projects.core.domain)
            implementation(projects.feature.authorization.domain.api)
            implementation(projects.feature.business.domain.api)
            implementation(projects.feature.authorization.data.source)
            implementation(projects.library.device.api)
        }
        commonTest.dependencies {
            implementation(projects.core.testFixtures)
        }
    }
}
