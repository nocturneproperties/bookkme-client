import build_src.constants.ApplicationConfig

plugins {
    alias(libs.plugins.convention.kmm.library.compose)
}

android {
    namespace = "${ApplicationConfig.ROOT_PACKAGE}.feature.dashboard"
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(projects.core)
            implementation(projects.core.presentation)
            implementation(projects.designsystem)
        }
    }
}

multiplatformResources {
    resourcesPackage.set("${ApplicationConfig.ROOT_PACKAGE}.feature.dashboard.resources")
    resourcesClassName.set("DashboardRes")
}