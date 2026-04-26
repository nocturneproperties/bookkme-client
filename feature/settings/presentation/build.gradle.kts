import build_src.constants.ApplicationConfig

plugins {
    id(libs.plugins.convention.kmm.library.compose.get().pluginId)
}

android {
    namespace = "${ApplicationConfig.ROOT_PACKAGE}.feature.settings"
}

multiplatformResources {
    resourcesPackage.set("${ApplicationConfig.ROOT_PACKAGE}.feature.settings.resources")
    resourcesClassName.set("SettingsRes")
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(projects.core)
            implementation(projects.core.presentation)
            implementation(projects.designsystem)
            implementation(projects.feature.settings.domain.api)
            implementation(projects.feature.authorization.domain.api)
            implementation(projects.library.device.api)
        }
    }
}