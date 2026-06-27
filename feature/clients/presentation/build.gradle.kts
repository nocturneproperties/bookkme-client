import build_src.constants.ApplicationConfig

plugins {
    id(libs.plugins.convention.kmm.library.compose.get().pluginId)
}

kotlin {
    android {
        namespace = "${ApplicationConfig.ROOT_PACKAGE}.feature.clients"
    }
    sourceSets {
        commonMain.dependencies {
            implementation(projects.core)
            implementation(projects.core.presentation)
            implementation(projects.designsystem)
            implementation(projects.feature.clients.domain.api)
            implementation(projects.library.device.api)
            implementation(projects.library.validation.api)
        }
    }
}
multiplatformResources {
    resourcesPackage.set("${ApplicationConfig.ROOT_PACKAGE}.feature.clients.resources")
    resourcesClassName.set("ClientsRes")
}