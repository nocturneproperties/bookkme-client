import build_src.constants.ApplicationConfig
import build_src.tools.libs

plugins {
    id(libs.plugins.convention.kmm.library.kotlin.get().pluginId)
    alias(libs.plugins.kmm.resources)
}

kotlin {
    android {
        namespace = "${ApplicationConfig.ROOT_PACKAGE}.library.notifications.impl"
    }
    sourceSets {
        androidMain.dependencies {
            implementation(project.dependencies.platform(libs.firebase.bom))
            implementation(libs.firebase.push)
            implementation(libs.androidx.core)
        }
        commonMain.dependencies {
            implementation(projects.core)
            implementation(projects.core.domain)
            implementation(libs.kmm.resources)
            implementation(projects.library.notifications.api)
        }
    }
}

multiplatformResources {
    resourcesPackage.set("${ApplicationConfig.ROOT_PACKAGE}.library.notifications.resources")
    resourcesClassName.set("NotifcationRes")
}
