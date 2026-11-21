import build_src.constants.ApplicationConfig
import build_src.tools.libs

plugins {
    id(libs.plugins.convention.kmm.library.kotlin.get().pluginId)
    alias(libs.plugins.kotlin.serialization)
}

kotlin {
    android {
        namespace = "${ApplicationConfig.ROOT_PACKAGE}.feature.authorization.data"
        optimization {
            consumerKeepRules.file("consumer-rules.pro")
        }
    }
    sourceSets {
        androidMain.dependencies {
            implementation(libs.androidx.credentials)
            implementation(libs.androidx.credentials.compat)
        }
        commonMain.dependencies {
            implementation(projects.core)
            implementation(projects.core.domain)
            implementation(projects.core.data)
            implementation(projects.database.schema)
            implementation(projects.feature.authorization.domain.api)
            implementation(projects.feature.authorization.data.source)
            implementation(projects.library.cache.api)
            implementation(libs.ktor.client.resources)
            implementation(libs.ktor.client.auth)
            implementation(libs.kotlin.serialization)
            implementation(projects.library.credentials.api)
        }
        commonTest.dependencies {
            implementation(libs.ktor.client.negotiation)
            implementation(libs.ktor.client.mock)
            implementation(projects.core.testFixtures)
        }
    }
}
