import build_src.constants.ApplicationConfig

plugins {
    id(libs.plugins.convention.kmm.database.get().pluginId)
}

kotlin {
    android {
        namespace = "${ApplicationConfig.ROOT_PACKAGE}.database"
    }
    sourceSets {
        commonMain.dependencies {
            implementation(projects.core)
        }
    }
}

room {
    schemaDirectory("$projectDir/schemas")
}
