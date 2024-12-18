import com.codingfeline.buildkonfig.compiler.FieldSpec
import me.bookk.build_src.constants.ProductFlavour

plugins {
    alias(libs.plugins.bookk.kmm.library)
    alias(libs.plugins.kmm.resources)
}

android {
    namespace = "me.bookk.shared"
}

buildkonfigExtend {
    forFlavour(ProductFlavour.DEV) {
        buildConfigField(FieldSpec.Type.STRING, "BASE_URL", "dev", const = true)
    }
    forFlavour(ProductFlavour.STAGE) {
        buildConfigField(FieldSpec.Type.STRING, "BASE_URL", "stage", const = true)
    }
    forFlavour(ProductFlavour.PROD) {
        buildConfigField(FieldSpec.Type.STRING, "BASE_URL", "prod", const = true)
    }
}

kotlin {
    sourceSets {
        androidMain.dependencies {
            implementation(libs.ktor.client.okhttp)
            implementation(libs.kotlinx.coroutines.android)
            implementation(libs.simplelogger.slf4j) // Required for ktor-logging support on JVM
        }
        commonMain.dependencies {
            // Projects
            implementation(projects.feature.authorization.presentation)
            implementation(projects.database)
            implementation(projects.core.data)
            api(projects.core)
            api(projects.core.domain)
            api(projects.core.di)
            api(projects.core.presentation)
            api(projects.designsystem)
            // Libs
            implementation(libs.koin.core)
            implementation(libs.ktor.client.negotiation)
            implementation(libs.ktor.client.protobuf)
            implementation(libs.ktor.client.logging)
            implementation(libs.androidx.preferences)
            implementation(libs.okio)
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