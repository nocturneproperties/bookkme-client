plugins {
    id(libs.plugins.convention.kmm.web.get().pluginId)
    alias(libs.plugins.compose.compiler)
}

group = "library.telegram"
version = "1.0-SNAPSHOT"

kotlin {
    js {
        browser {
            testTask { enabled = false }
        }
        nodejs {
            testTask { useMocha { timeout = "10s" } }
        }
    }

    sourceSets {
        jsMain.dependencies {
            api(projects.designsystem)
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
