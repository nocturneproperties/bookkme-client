import build_src.constants.ApplicationConfig

plugins {
    id(libs.plugins.convention.kmm.database.get().pluginId)
}

kotlin {
    android {
        namespace = "${ApplicationConfig.ROOT_PACKAGE}.database.business"
    }
    sourceSets {
        commonMain.dependencies {
            api(projects.database.schema)
            implementation(projects.core)
        }
        commonTest.dependencies {
            implementation(projects.core.testFixtures)
        }
        getByName("androidHostTest").dependencies {
            runtimeOnly("androidx.sqlite:sqlite-bundled-jvm:${libs.versions.sqlite.get()}")
        }
    }
}

room3 {
    schemaDirectory("$projectDir/schemas")
}
