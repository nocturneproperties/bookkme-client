import build_src.constants.ApplicationConfig

plugins {
    id(libs.plugins.convention.kmm.database.get().pluginId)
}

kotlin {
    android {
        namespace = "${ApplicationConfig.ROOT_PACKAGE}.database.client"
    }
    sourceSets {
        commonMain.dependencies {
            api(projects.database.account)
            implementation(projects.core)
        }
    }
}

room3 {
    schemaDirectory("$projectDir/schemas")
}
