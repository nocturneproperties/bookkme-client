import build_src.constants.ApplicationConfig

plugins {
    id(libs.plugins.convention.kmm.library.compose.get().pluginId)
}

android {
    namespace = "${ApplicationConfig.ROOT_PACKAGE}.feature.appointments"
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(projects.core)
            implementation(projects.core.presentation)
            implementation(projects.designsystem)
            implementation(projects.feature.appointments.domain.api)
        }
    }
}

multiplatformResources {
    resourcesPackage.set("${ApplicationConfig.ROOT_PACKAGE}.feature.appointments.resources")
    resourcesClassName.set("AppointmentsRes")
}