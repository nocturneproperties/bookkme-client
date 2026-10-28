import build_src.constants.ApplicationConfig

plugins {
    id(libs.plugins.convention.kmm.library.compose.get().pluginId)
}

kotlin {
    android {
        namespace = "${ApplicationConfig.ROOT_PACKAGE}.designsystem"
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
    resourcesPackage.set("${ApplicationConfig.ROOT_PACKAGE}.designsystem.resources")
    resourcesClassName.set("DesignSystem")
    iosMinimalDeploymentTarget.set("26.0")
}
