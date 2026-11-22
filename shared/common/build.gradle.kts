import build_src.constants.ApplicationConfig
import build_src.constants.ProductFlavour
import build_src.tools.findLocalProperty
import build_src.tools.getCurrentVariant
import com.codingfeline.buildkonfig.compiler.FieldSpec.Type.BOOLEAN
import com.codingfeline.buildkonfig.compiler.FieldSpec.Type.STRING

plugins {
    id(libs.plugins.convention.kmm.library.kotlin.get().pluginId)
    alias(libs.plugins.kmm.resources)
    alias(libs.plugins.buldconfig)
}

kotlin {
    android {
        namespace = "${ApplicationConfig.ROOT_PACKAGE}.shared.common"
    }
    sourceSets {
        commonMain.dependencies {
            implementation(projects.core)
            implementation(projects.core.domain)
            implementation(projects.core.data)
            implementation(projects.core.presentation)
            implementation(projects.designsystem)
            implementation(projects.library.device.api)
            implementation(projects.feature.authorization.domain.api)
            implementation(libs.ktor.client.negotiation)
            implementation(libs.ktor.client.protobuf)
            implementation(libs.ktor.client.logging)
            implementation(libs.ktor.client.resources)
            implementation(libs.ktor.client.auth)
            implementation(libs.logger)
            implementation(libs.kmm.resources)
        }
        commonTest.dependencies {
            implementation(projects.core.testFixtures)
        }
        androidMain.dependencies {
            implementation(libs.ktor.client.okhttp)
        }
        iosMain.dependencies {
            implementation(libs.ktor.client.darwin)
        }
    }
}

val proxyUrl = findLocalProperty("bookk.proxyUrl").orEmpty()

buildkonfig {
    packageName = "me.bookk.shared.common"
    defaultConfigs {
        buildConfigField(STRING, "BASE_URL", "", const = true)
        buildConfigField(STRING, "SITE_URL", "https://bookkme.com", const = true)
        buildConfigField(
            BOOLEAN,
            "DEBUG",
            getCurrentVariant().contains("debug", ignoreCase = true).toString(),
            const = true,
        )
        buildConfigField(
            STRING,
            "VERSION_NAME",
            ApplicationConfig.VERSION_NAME,
            const = true,
        )
        buildConfigField(
            STRING,
            "PROXY_URL",
            if (getCurrentVariant().contains("debug", ignoreCase = true)) proxyUrl else "",
            const = true,
        )
    }
    defaultConfigs(ProductFlavour.DEV.title + "Debug") {
        buildConfigField(
            STRING,
            "BASE_URL",
            "https://local.bookkme.app/api",
            const = true,
        )
        buildConfigField(STRING, "SITE_URL", "https://local.bookkme.app", const = true)
    }
    defaultConfigs(ProductFlavour.DEV.title + "Release") {
        buildConfigField(
            STRING,
            "BASE_URL",
            "https://local.bookkme.app/api",
            const = true,
        )
        buildConfigField(STRING, "SITE_URL", "https://local.bookkme.app", const = true)
    }
    defaultConfigs(ProductFlavour.PROD.title + "Release") {
        buildConfigField(
            STRING,
            "BASE_URL",
            "https://bookkme.app/api",
            const = true,
        )
    }
    targetConfigs {
        create("js") {
            buildConfigField(STRING, "PROXY_URL", "", const = true)
        }
    }
}
