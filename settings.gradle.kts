enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")
pluginManagement {
    includeBuild("build-src")
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "Bookk"

//Shared
include(":shared")

//App
include(":androidApp")

//Core
include(":designsystem")
include(":core")
include(":core:data")
include(":core:domain")
include(":core:presentation")
include(":core:di")

//Domain
include(":domain:environment:api")
include(":domain:environment:impl")
