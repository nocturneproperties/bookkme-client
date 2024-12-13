plugins {
    alias(libs.plugins.bookk.kmm.library.compose)
    alias(libs.plugins.bookk.localise)
    alias(libs.plugins.kmm.resources)
}

android {
    namespace = "me.bookk.designsystem"
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(projects.core.presentation)
            implementation(projects.domain.environment.api)
        }
    }
}

localise.config {
    tag = "designsystem"
    format = "android"
    fileName = "strings"
    resDir = "${projectDir}/src/commonMain/moko-resources"
    languages = arrayOf("en")
}

localise.config {
    tag = "plurals"
    format = "android"
    fileName = "plurals"
    resDir = "${projectDir}/src/commonMain/moko-resources"
    languages = arrayOf("en")
}

multiplatformResources {
    resourcesPackage.set("me.bookk.designsystem.resources")
    resourcesClassName.set("DesignSystem")
}
