import com.varabyte.kobweb.gradle.library.util.configAsKobwebLibrary

plugins {
    id(libs.plugins.convention.kmm.web.get().pluginId)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.kobweb.library)
    alias(libs.plugins.kmm.resources)
}

group = "me.bookk.site.designsystem"
version = "1.0-SNAPSHOT"

kotlin {
    configAsKobwebLibrary()
    js {
        nodejs {
            testTask { useMocha { timeout = "10s" } }
        }
    }

    sourceSets {
        jsMain.dependencies {
            api(projects.designsystem)
            api(projects.core.presentation)
            implementation(projects.core)
            implementation(libs.kotlinx.datetime)
            api(libs.kmm.resources)
            implementation(libs.kobweb.compose.runtime)
            implementation(libs.compose.html.core)
            implementation(libs.kobweb.core)
            implementation(libs.kobweb.silk)
        }
        jsTest.dependencies {
            implementation(projects.core.testFixtures)
            implementation(libs.test.kotlin)
            implementation(libs.kotlinx.coroutines.test)
        }
    }
}

multiplatformResources {
    resourcesPackage.set("me.bookk.site.designsystem.resources")
    resourcesClassName.set("WebDesignSystem")
}
