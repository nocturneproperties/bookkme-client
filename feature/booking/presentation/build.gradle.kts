import build_src.constants.ApplicationConfig
import org.jetbrains.kotlin.gradle.plugin.KotlinPlatformType

plugins {
    id(libs.plugins.convention.kmm.library.compose.get().pluginId)
}

kotlin {
    android {
        namespace = "${ApplicationConfig.ROOT_PACKAGE}.feature.booking"
    }
    sourceSets {
        commonMain.dependencies {
            implementation(projects.core)
            implementation(projects.core.presentation)
            implementation(projects.designsystem)
            implementation(projects.library.money)
            implementation(projects.feature.booking.domain.api)
        }
        jsMain.dependencies {
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
    resourcesPackage.set("${ApplicationConfig.ROOT_PACKAGE}.feature.booking.resources")
    resourcesClassName.set("BookingRes")
}
