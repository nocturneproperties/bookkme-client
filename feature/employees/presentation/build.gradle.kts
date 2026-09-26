import build_src.constants.ApplicationConfig

plugins {
    id(libs.plugins.convention.kmm.library.compose.get().pluginId)
}

kotlin {
    android {
        namespace = "${ApplicationConfig.ROOT_PACKAGE}.feature.employees"
    }
    sourceSets {
        commonMain.dependencies {
            implementation(projects.core)
            implementation(projects.core.presentation)
            implementation(projects.designsystem)
            implementation(projects.feature.employees.domain.api)
            implementation(projects.library.device.api)
        }
        commonTest.dependencies {
            implementation(projects.core.testFixtures)
            implementation(projects.designsystem.testFixtures)
        }
    }
}

multiplatformResources {
    resourcesPackage.set("${ApplicationConfig.ROOT_PACKAGE}.feature.employees.resources")
    resourcesClassName.set("EmployeesRes")
}
