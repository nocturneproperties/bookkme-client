plugins {
    id(libs.plugins.convention.kmm.library.kotlin.get().pluginId)
    alias(libs.plugins.kotlin.serialization)
}

kotlin {
    android {
        namespace = "me.bookk.core.domain"
        optimization {
            consumerKeepRules.file("consumer-rules.pro")
        }
    }
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            api(projects.core)
            implementation(libs.kotlin.serialization.core)
        }
    }
}