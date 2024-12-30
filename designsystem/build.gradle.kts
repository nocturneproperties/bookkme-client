plugins {
    alias(libs.plugins.bookk.kmm.library.compose)
}

android {
    namespace = "me.bookk.designsystem"
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(projects.core.presentation)
            implementation(projects.domain.environment.api)
        }
    }
}

multiplatformResources {
    resourcesPackage.set("me.bookk.designsystem.resources")
    resourcesClassName.set("DesignSystem")
    iosMinimalDeploymentTarget
}
