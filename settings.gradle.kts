enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")
pluginManagement {
    includeBuild("build-src")
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}
plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
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
include(":environment:api")
include(":environment:impl")

//Library
include(":library:cache:api")
include(":library:cache:impl")
include(":library:credentials:api")
include(":library:credentials:impl")
include(":library:permissions:api")
include(":library:permissions:impl")
include(":library:files:api")
include(":library:files:impl")
include(":library:device:api")
include(":library:device:impl")
include(":library:money:api")
include(":library:money:impl")
include(":library:biometry:api")
include(":library:biometry:impl")
include(":library:validation:api")
include(":library:validation:impl")

//Authorization
include(":feature:authorization:data")
include(":feature:authorization:data:source")
include(":feature:authorization:domain:api")
include(":feature:authorization:domain:impl")
include(":feature:authorization:presentation")

//Settings
include(":feature:settings:data")
include(":feature:settings:domain:api")
include(":feature:settings:domain:impl")
include(":feature:settings:domain:datasource")
include(":feature:settings:presentation")

//Dashboard
include(":feature:dashboard:presentation")

//Business
include(":feature:business:data")
include(":feature:business:data:source")
include(":feature:business:domain:api")
include(":feature:business:domain:impl")
include(":feature:business:presentation")

//Clients
include(":feature:clients:data")
include(":feature:clients:data:source")
include(":feature:clients:domain:api")
include(":feature:clients:domain:impl")
include(":feature:clients:presentation")
