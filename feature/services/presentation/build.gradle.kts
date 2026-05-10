import build_src.constants.ApplicationConfig

plugins {
    id(libs.plugins.convention.kmm.library.compose.get().pluginId)
}

android {
    namespace = "${ApplicationConfig.ROOT_PACKAGE}.feature.services"
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(projects.core)
            implementation(projects.core.presentation)
            implementation(projects.designsystem)
            implementation(projects.feature.services.domain.api)
        }
    }
}

multiplatformResources {
    resourcesPackage.set("${ApplicationConfig.ROOT_PACKAGE}.feature.services.resources")
    resourcesClassName.set("ServicesRes")
}