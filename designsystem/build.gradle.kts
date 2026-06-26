plugins {
    id(libs.plugins.convention.kmm.library.compose.get().pluginId)
}

kotlin {
    android {
        namespace = "me.bookk.designsystem"
        optimization {
            consumerKeepRules.file("consumer-rules.pro")
        }
    }
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(projects.core.presentation)
            implementation(projects.environment.api)
            implementation(projects.library.money)
        }
    }
}

multiplatformResources {
    resourcesPackage.set("me.bookk.designsystem.resources")
    resourcesClassName.set("DesignSystem")
    iosMinimalDeploymentTarget
}
