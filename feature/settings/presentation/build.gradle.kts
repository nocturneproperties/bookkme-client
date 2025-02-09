import me.bookk.build_src.constants.AndroidConfig

plugins {
    alias(libs.plugins.bookk.kmm.library.compose)
}

android {
    namespace = "${AndroidConfig.ROOT_PACKAGE}.feature.settings"
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(projects.core)
            implementation(projects.core.presentation)
            implementation(projects.designsystem)
            implementation(projects.feature.settings.domain.api)
        }
    }
}

multiplatformResources {
    resourcesPackage.set("${AndroidConfig.ROOT_PACKAGE}.feature.settings.resources")
    resourcesClassName.set("SettingsRes")
}