import build_src.constants.AndroidConfig

plugins {
    alias(libs.plugins.convention.kmm.library.compose)
}

android {
    namespace = "${AndroidConfig.ROOT_PACKAGE}.feature.authorization"
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(projects.core)
            implementation(projects.core.presentation)
            implementation(projects.designsystem)
            implementation(projects.feature.authorization.domain.api)
            implementation(projects.feature.settings.domain.api)
            implementation(projects.library.device.api)
        }
    }
}

multiplatformResources {
    resourcesPackage.set("${AndroidConfig.ROOT_PACKAGE}.feature.authorization.resources")
    resourcesClassName.set("AuthRes")
}