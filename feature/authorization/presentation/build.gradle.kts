import me.bookk.build_src.constants.AndroidConfig

plugins {
    alias(libs.plugins.bookk.kmm.library.compose)
}

android {
    namespace = "${AndroidConfig.ROOT_PACKAGE}.feature.sign_up"
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(projects.core)
            implementation(projects.core.presentation)
            implementation(projects.designsystem)
            implementation(projects.feature.authorization.domain.api)
        }
    }
}

multiplatformResources {
    resourcesPackage.set("${AndroidConfig.ROOT_PACKAGE}.feature.sign_up.resources")
    resourcesClassName.set("SignUpRes")
}