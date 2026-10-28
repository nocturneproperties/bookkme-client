import build_src.constants.ApplicationConfig

plugins {
    id(libs.plugins.convention.kmm.library.kotlin.get().pluginId)
    alias(libs.plugins.kotlin.serialization)
}

kotlin {
    android {
        namespace = "${ApplicationConfig.ROOT_PACKAGE}.core.domain"
        optimization {
            consumerKeepRules.file("consumer-rules.pro")
        }
    }

    sourceSets {
        commonMain.dependencies {
            api(projects.core)
            implementation(libs.kotlin.serialization.core)
        }
    }
}