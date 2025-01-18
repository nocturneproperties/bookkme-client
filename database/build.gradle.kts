plugins {
    alias(libs.plugins.bookk.kmm.database)
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
