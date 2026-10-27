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

    sourceSets {
        commonMain.dependencies {
            implementation(projects.core.presentation)
            implementation(projects.environment.api)
            implementation(projects.library.money)
        }
        commonTest.dependencies {
            implementation(projects.core.testFixtures)
        }
    }
}

multiplatformResources {
    resourcesPackage.set("me.bookk.designsystem.resources")
    resourcesClassName.set("DesignSystem")
    iosMinimalDeploymentTarget.set("26.0")
}
