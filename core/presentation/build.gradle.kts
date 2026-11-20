import build_src.constants.ApplicationConfig
import org.jetbrains.kotlin.gradle.plugin.KotlinPlatformType

plugins {
    id(libs.plugins.convention.kmm.library.compose.get().pluginId)
}

kotlin {
    android {
        namespace = "${ApplicationConfig.ROOT_PACKAGE}.core.presentation"
        optimization {
            consumerKeepRules.file("consumer-rules.pro")
        }
    }
    sourceSets {
        commonMain.dependencies {
            implementation(projects.core)
        }
        jsMain.dependencies {
            implementation(libs.kobweb.compose.runtime)
        }
        commonTest.dependencies {
            implementation(projects.core.testFixtures)
        }
    }
}

composeCompiler {
    targetKotlinPlatforms.add(KotlinPlatformType.js)
}

multiplatformResources {
    resourcesPackage.set("${ApplicationConfig.ROOT_PACKAGE}.core.presentation")
    resourcesClassName.set("CorePresentation")
}
