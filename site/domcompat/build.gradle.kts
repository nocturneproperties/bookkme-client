plugins {
    id(libs.plugins.convention.kmm.web.get().pluginId)
}

group = "me.bookk.site.domcompat"
version = "1.0-SNAPSHOT"

kotlin {
    js {
        browser()
    }

    sourceSets {
        jsMain.dependencies {
            api(libs.kotlinx.browser)
        }
    }
}

configurations.configureEach {
    exclude(group = "org.jetbrains.kotlin", module = "kotlin-dom-api-compat")
}
