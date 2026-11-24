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
            api(libs.androidx.activity)
        }
        commonTest.dependencies {
            implementation(projects.core.testFixtures)
        }
        jsMain.dependencies {
            implementation(npm("@js-joda/timezone", libs.versions.js.joda.timezone.get()))
        }
    }
}
