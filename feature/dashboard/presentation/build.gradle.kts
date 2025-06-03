import build_src.constants.AndroidConfig

plugins {
    alias(libs.plugins.convention.kmm.library.compose)
}

android {
    namespace = "${AndroidConfig.ROOT_PACKAGE}.feature.dashboard"
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
    resourcesPackage.set("${AndroidConfig.ROOT_PACKAGE}.feature.dashboard.resources")
    resourcesClassName.set("DashboardRes")
}