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

//Platform
include(":feature:platform:data")
include(":feature:platform:domain:api")
include(":feature:platform:domain:impl")
include(":feature:platform:domain:datasource")

//Authorization
include(":feature:authorization:data")
include(":feature:authorization:domain:api")
include(":feature:authorization:domain:impl")
include(":feature:authorization:domain:datasource")
include(":feature:authorization:presentation")

//Settings
include(":feature:settings:data")
include(":feature:settings:domain:api")
include(":feature:settings:domain:impl")
include(":feature:settings:domain:datasource")
include(":feature:settings:presentation")

//Dashboard
include(":feature:dashboard:presentation")
