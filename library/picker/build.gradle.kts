import build_src.constants.ApplicationConfig

plugins {
    id(libs.plugins.convention.kmm.library.compose.get().pluginId)
}

kotlin {
    android {
        namespace = "${ApplicationConfig.ROOT_PACKAGE}.library.picker"
    }
    sourceSets {
        commonMain.dependencies {
            implementation(projects.core.presentation)
            implementation(projects.core)
            implementation(projects.core.domain)
            implementation(projects.designsystem)
        }
    }
}

multiplatformResources {
    resourcesPackage.set("${ApplicationConfig.ROOT_PACKAGE}.library.picker.resources")
    resourcesClassName.set("PickerRes")
}
