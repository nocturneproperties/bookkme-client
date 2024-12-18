import me.bookk.build_src.constants.AndroidConfig

plugins {
    alias(libs.plugins.bookk.android.application)
}

android {
    namespace = AndroidConfig.ROOT_PACKAGE

    defaultConfig {
        applicationId = AndroidConfig.ROOT_PACKAGE
        versionCode = AndroidConfig.VERSION_CODE
        versionName = AndroidConfig.VERSION_NAME
    }
}

dependencies {
    implementation(projects.shared)
    implementation(projects.feature.authorization.presentation)
}