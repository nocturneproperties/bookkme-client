import build_src.tools.getCurrentVariant
import com.codingfeline.buildkonfig.compiler.FieldSpec.Type.BOOLEAN

plugins {
    id(libs.plugins.convention.kmm.library.kotlin.get().pluginId)
    alias(libs.plugins.buldconfig)
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "me.bookk.core.data"
}

buildkonfig {
    packageName = "me.bookk.core.data"
    defaultConfigs {
        buildConfigField(
            BOOLEAN,
            "DEBUG",
            getCurrentVariant().contains("debug", ignoreCase = true).toString(),
            const = true
        )
    }
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            api(libs.ktor.client.core)
            api(libs.ktor.client.mock)
            api(libs.ktor.client.protobuf)
            api(projects.core.domain)
        }
    }
}