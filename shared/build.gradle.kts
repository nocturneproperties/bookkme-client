plugins {
    alias(libs.plugins.bookk.kmm.library)
    alias(libs.plugins.kmm.resources)
}

kotlin {
    sourceSets {
        androidMain.dependencies {
            implementation(libs.ktor.client.okhttp)
            implementation(libs.kotlinx.coroutines.android)
            implementation(libs.simplelogger.slf4j) // Required for ktor-logging support on JVM
        }
        commonMain.dependencies {
            implementation(libs.koin.core)
            implementation(libs.ktor.client.negotiation)
            implementation(libs.ktor.client.protobuf)
            implementation(libs.ktor.client.logging)
            api(projects.core)
            api(projects.core.domain)
            api(projects.core.di)
            api(projects.core.presentation)
            api(projects.designsystem)
            api(libs.kotlinx.datetime)
            api(libs.kmm.resources)
        }
        iosMain.dependencies {
            implementation(libs.ktor.client.darwin)
        }

    }
    listOf(
        iosX64(),
        iosArm64(),
        iosSimulatorArm64()
    ).forEach {
        it.binaries.framework {
            baseName = project.name
            isStatic = false
            export(projects.core)
            export(projects.core.domain)
            export(projects.core.di)
            export(projects.core.presentation)
            export(projects.designsystem)
            export(libs.kmm.resources)
            export(libs.kotlinx.datetime)
            export(libs.kmm.resources.graphics)
        }
    }
}

android {
    namespace = "me.bookk.shared"
}
