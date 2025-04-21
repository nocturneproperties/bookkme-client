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
        buildConfigField(FieldSpec.Type.STRING, "BASE_URL", "https://local.bookkk.me/api", const = true)
    }
    forFlavour(ProductFlavour.STAGE) {
        buildConfigField(FieldSpec.Type.STRING, "BASE_URL", "https://bookkk.me/api", const = true)
    }
    forFlavour(ProductFlavour.PROD) {
        buildConfigField(FieldSpec.Type.STRING, "BASE_URL", "https://bookkk.me/api", const = true)
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
            //Core
            implementation(projects.database)
            implementation(projects.core.data)
            api(projects.core)
            api(projects.core.domain)
            api(projects.core.presentation)
            api(projects.designsystem)
            //Auth
            implementation(projects.feature.authorization.domain.api)
            implementation(projects.feature.authorization.domain.impl)
            implementation(projects.feature.authorization.data)
            api(projects.feature.authorization.presentation)
            //Platform
            implementation(projects.feature.platform.domain.api)
            implementation(projects.feature.platform.domain.impl)
            implementation(projects.feature.platform.data)
            //Dashboard
            api(projects.feature.dashboard.presentation)
            //Settings
            implementation(projects.feature.settings.domain.api)
            implementation(projects.feature.settings.domain.impl)
            implementation(projects.feature.settings.data)
            api(projects.feature.settings.presentation)
            // Libs
            implementation(libs.koin.core)
            implementation(libs.ktor.client.negotiation)
            implementation(libs.ktor.client.protobuf)
            implementation(libs.ktor.client.logging)
            implementation(libs.ktor.client.resources)
            implementation(libs.ktor.client.auth)
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
            export(libs.kotlinx.datetime)
            export(libs.kmm.resources)
            export(libs.kmm.resources.graphics)
            export(projects.core)
            export(projects.core.domain)
            export(projects.core.presentation)
            export(projects.designsystem)
            export(projects.feature.authorization.presentation)
            export(projects.feature.dashboard.presentation)
            export(projects.feature.settings.presentation)
        }
    }
}