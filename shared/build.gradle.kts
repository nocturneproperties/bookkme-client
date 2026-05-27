import build_src.constants.ApplicationConfig
import build_src.constants.ProductFlavour
import build_src.tools.getCurrentVariant
import com.codingfeline.buildkonfig.compiler.FieldSpec.Type.BOOLEAN
import com.codingfeline.buildkonfig.compiler.FieldSpec.Type.STRING

plugins {
    id(libs.plugins.convention.kmm.library.kotlin.get().pluginId)
    alias(libs.plugins.buldconfig)
    alias(libs.plugins.kmm.resources)
}

android {
    namespace = "me.bookk.shared"
}

buildkonfig {
    packageName = "me.bookk.shared"
    defaultConfigs {
        buildConfigField(STRING, "BASE_URL", "", const = true)
        buildConfigField(
            BOOLEAN,
            "DEBUG",
            getCurrentVariant().contains("debug", ignoreCase = true).toString(),
            const = true
        )
        buildConfigField(
            STRING,
            "VERSION_NAME",
            ApplicationConfig.VERSION_NAME,
            const = true
        )
        buildConfigField(STRING, "VARIANT", getCurrentVariant(), const = true)
    }
    defaultConfigs(ProductFlavour.DEV.title + "Debug") {
        buildConfigField(
            STRING,
            "BASE_URL",
            "https://local.bookkme.app/api",
            const = true
        )
    }
    defaultConfigs(ProductFlavour.DEV.title + "Release") {
        buildConfigField(
            STRING,
            "BASE_URL",
            "https://local.bookkme.app/api",
            const = true
        )
    }
    defaultConfigs(ProductFlavour.PROD.title + "Release") {
        buildConfigField(
            STRING,
            "BASE_URL",
            "https://bookkme.app/api",
            const = true
        )
    }
}

kotlin {
    sourceSets {
        androidMain.dependencies {
            implementation(libs.ktor.client.okhttp)
            implementation(libs.kotlinx.coroutines.android)
        }
        commonMain.dependencies {
            //Core
            implementation(projects.database)
            implementation(projects.core.data)
            implementation(projects.environment.api)
            api(projects.core)
            api(projects.core.domain)
            api(projects.core.presentation)
            api(projects.designsystem)
            //Library
            implementation(projects.library.device.api)
            implementation(projects.library.device.impl)
            implementation(projects.library.cache.api)
            implementation(projects.library.cache.impl)
            implementation(projects.library.permissions.api)
            implementation(projects.library.permissions.impl)
            implementation(projects.library.files.api)
            implementation(projects.library.files.impl)
            implementation(projects.library.validation.api)
            implementation(projects.library.validation.impl)
            api(projects.library.credentials.api)
            api(projects.library.credentials.impl)
            api(projects.library.money.api)
            api(projects.library.biometry.api)
            api(projects.library.picker)
            implementation(projects.library.biometry.impl)
            implementation(projects.library.money.impl)
            //Auth
            implementation(projects.feature.authorization.domain.api)
            implementation(projects.feature.authorization.domain.impl)
            implementation(projects.feature.authorization.data)
            api(projects.feature.authorization.presentation)
            //Dashboard
            api(projects.feature.dashboard.presentation)
            //Settings
            implementation(projects.feature.settings.domain.api)
            implementation(projects.feature.settings.domain.impl)
            implementation(projects.feature.settings.data)
            api(projects.feature.settings.presentation)
            //Business
            implementation(projects.feature.business.domain.api)
            implementation(projects.feature.business.domain.impl)
            implementation(projects.feature.business.data)
            api(projects.feature.business.presentation)
            //Clients
            implementation(projects.feature.clients.domain.api)
            implementation(projects.feature.clients.domain.impl)
            implementation(projects.feature.clients.data)
            api(projects.feature.clients.presentation)
            //Clients
            implementation(projects.feature.services.domain.api)
            implementation(projects.feature.services.domain.impl)
            implementation(projects.feature.services.data)
            api(projects.feature.services.presentation)
            // Libs
            implementation(libs.koin.core)
            implementation(libs.ktor.client.mock)
            implementation(libs.ktor.client.negotiation)
            implementation(libs.ktor.client.protobuf)
            implementation(libs.ktor.client.logging)
            implementation(libs.ktor.client.resources)
            implementation(libs.ktor.client.auth)
            implementation(libs.koin.core)
            implementation(libs.logger)
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
            export(projects.feature.business.presentation)
            export(projects.feature.clients.presentation)
            export(projects.feature.services.presentation)
            export(projects.library.money.api)
            export(projects.library.credentials.api)
            export(projects.library.biometry.api)
            export(projects.library.picker)
        }
    }
}