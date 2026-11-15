import com.varabyte.kobweb.gradle.application.util.configAsKobwebApplication

plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.kobweb.application)
    alias(libs.plugins.kmm.resources)
}

group = "me.bookk.site"
version = "0.1"

kobweb {
    app {
        index {
            description.set("BookkMe — a booking app for small businesses on Android and iOS.")
            faviconPath.set("/favicon.svg")
        }
    }
}

kotlin {
    configAsKobwebApplication("site")

    sourceSets {
        jsMain.dependencies {
            implementation(projects.site.designsystem)
            implementation(libs.kmm.resources)
            implementation(libs.kobweb.compose.runtime)
            implementation(libs.compose.html.core)
            implementation(libs.kobweb.core)
            implementation(libs.kobweb.silk)
        }
    }
}

multiplatformResources {
    resourcesPackage.set("me.bookk.site.resources")
    resourcesClassName.set("SiteRes")
}
