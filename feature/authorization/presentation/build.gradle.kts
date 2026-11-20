import build_src.constants.ApplicationConfig
import org.jetbrains.kotlin.gradle.plugin.KotlinPlatformType

plugins {
    id(libs.plugins.convention.kmm.library.compose.get().pluginId)
}

kotlin {
    android {
        namespace = "${ApplicationConfig.ROOT_PACKAGE}.feature.authorization"
        optimization {
            consumerKeepRules.file("consumer-rules.pro")
        }
    }
    sourceSets {
        commonMain.dependencies {
            implementation(projects.core)
            implementation(projects.core.domain)
            implementation(projects.core.presentation)
            implementation(projects.designsystem)
            implementation(projects.feature.authorization.domain.api)
            implementation(projects.library.device.api)
            implementation(projects.library.validation.api)
        }
        jsMain.dependencies {
            implementation(projects.site.designsystem)
            implementation(projects.library.telegram)
            implementation(libs.kobweb.compose.runtime)
            implementation(libs.compose.html.core)
            implementation(libs.kobweb.silk)
        }
        commonTest.dependencies {
            implementation(projects.core.testFixtures)
            implementation(projects.designsystem.testFixtures)
        }
    }
}

composeCompiler {
    targetKotlinPlatforms.add(KotlinPlatformType.js)
}

multiplatformResources {
    resourcesPackage.set("${ApplicationConfig.ROOT_PACKAGE}.feature.authorization.resources")
    resourcesClassName.set("AuthRes")
}
