import build_src.constants.ApplicationConfig

plugins {
    id(libs.plugins.convention.kmm.library.kotlin.get().pluginId)
}

kotlin {
    android {
        namespace = "${ApplicationConfig.ROOT_PACKAGE}.core"
        optimization {
            consumerKeepRules.file("consumer-rules.pro")
        }
    }
    sourceSets {
        androidMain.dependencies {
            implementation(libs.androidx.core)
            implementation(libs.androidx.activity)
        }
    }
}