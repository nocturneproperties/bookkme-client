plugins {
    id(libs.plugins.convention.kmm.database.get().pluginId)
}

android {
    namespace = "me.bookk.database"
}

room {
    schemaDirectory("$projectDir/schemas")
}

kotlin {
    sourceSets.commonMain.dependencies {
        implementation(projects.core)
    }
}
