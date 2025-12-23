import build_src.constants.ApplicationConfig

plugins {
    alias(libs.plugins.convention.kmm.library.compose)
}

android {
    namespace = "${ApplicationConfig.ROOT_PACKAGE}.feature.business"
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(projects.core)
            implementation(projects.core.presentation)
            implementation(projects.designsystem)
            implementation(projects.feature.business.domain.api)
            implementation(projects.library.device.api)
        }
    }
}

multiplatformResources {
    resourcesPackage.set("${ApplicationConfig.ROOT_PACKAGE}.feature.business.resources")
    resourcesClassName.set("BusinessRes")
}