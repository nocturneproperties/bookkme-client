plugins {
    id(libs.plugins.convention.kmm.database.get().pluginId)
}

kotlin {
    android {
        namespace = "me.bookk.database"
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
