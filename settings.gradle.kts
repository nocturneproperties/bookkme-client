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
include(":database")

//App
include(":androidApp")

//Core
include(":designsystem")
include(":core")
include(":core:data")
include(":core:domain")
include(":core:presentation")

//Domain
include(":domain:environment:api")
include(":domain:environment:impl")

//Authorization
include(":feature:authorization:data")
include(":feature:authorization:domain:api")
include(":feature:authorization:domain:impl")
include(":feature:authorization:domain:datasource")
include(":feature:authorization:presentation")

//Platform
include(":feature:platform:data")
include(":feature:platform:domain:api")
include(":feature:platform:domain:impl")
include(":feature:platform:domain:datasource")

//Dashboard
include(":feature:dashboard:presentation")
