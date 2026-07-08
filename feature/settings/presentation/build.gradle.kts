import build_src.constants.ApplicationConfig
import build_src.tools.libs

plugins {
    id(libs.plugins.convention.kmm.library.compose.get().pluginId)
}

kotlin {
    android {
        namespace = "${ApplicationConfig.ROOT_PACKAGE}.feature.settings"
    }
    sourceSets {
        androidMain.dependencies {
            implementation(project.dependencies.platform(libs.firebase.bom))
            implementation(libs.firebase.push)
        }
        commonMain.dependencies {
            implementation(projects.core)
            implementation(projects.core.presentation)
            implementation(projects.designsystem)
            implementation(projects.feature.authorization.domain.api)
            implementation(projects.feature.settings.domain.api)
            implementation(projects.library.validation.api)
            implementation(projects.library.device.api)
            implementation(projects.library.permissions.api)
        }
    }
}

multiplatformResources {
    resourcesPackage.set("${ApplicationConfig.ROOT_PACKAGE}.feature.settings.resources")
    resourcesClassName.set("SettingsRes")
}