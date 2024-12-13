import me.bookk.build_src.constants.AndroidConfig

plugins {
    alias(libs.plugins.bookk.android.application)
}

android {
    namespace = "me.bookk.android"

    defaultConfig {
        applicationId = "me.bookk.android"
        versionCode = AndroidConfig.VERSION_CODE
        versionName = AndroidConfig.VERSION_NAME
    }
}

dependencies {
    implementation(projects.shared)
}