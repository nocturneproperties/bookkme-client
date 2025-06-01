plugins {
    alias(libs.plugins.convention.kmm.database)
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
