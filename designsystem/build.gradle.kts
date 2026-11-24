import build_src.constants.ApplicationConfig
import com.varabyte.kobweb.gradle.library.util.configAsKobwebLibrary
import org.jetbrains.kotlin.gradle.plugin.KotlinPlatformType

plugins {
    id(libs.plugins.convention.kmm.library.compose.get().pluginId)
    alias(libs.plugins.kobweb.library)
}

kotlin {
    configAsKobwebLibrary()
    android {
        namespace = "${ApplicationConfig.ROOT_PACKAGE}.designsystem"
        optimization {
            consumerKeepRules.file("consumer-rules.pro")
        }
    }

    sourceSets {
        commonMain.dependencies {
            implementation(projects.core)
            implementation(projects.core.presentation)
            implementation(projects.library.money)
        }
        jsMain.dependencies {
            api(projects.core.presentation)
            api(libs.kmm.resources)
            implementation(libs.kotlinx.datetime)
            implementation(libs.kobweb.compose.runtime)
            implementation(libs.compose.html.core)
            implementation(libs.kobweb.core)
            implementation(libs.kobweb.silk)
        }
        commonTest.dependencies {
            implementation(projects.core.testFixtures)
        }
    }
}

composeCompiler {
    targetKotlinPlatforms.set(setOf(KotlinPlatformType.androidJvm, KotlinPlatformType.js))
    includeSourceInformation.set(true)
    includeTraceMarkers.set(true)
}

multiplatformResources {
    resourcesPackage.set("${ApplicationConfig.ROOT_PACKAGE}.designsystem.resources")
    resourcesClassName.set("DesignSystem")
    iosMinimalDeploymentTarget.set("26.0")
}
