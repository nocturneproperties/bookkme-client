import build_src.tools.getCurrentVariant
import build_src.tools.libs
import com.codingfeline.buildkonfig.compiler.FieldSpec.Type.STRING

plugins {
    id(libs.plugins.convention.kmm.library.kotlin.get().pluginId)
    alias(libs.plugins.buldconfig)
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "me.bookk.feature.authorization.data"
}

buildkonfig {
    packageName = "me.bookk.feature.authorization.data"
    defaultConfigs {
        buildConfigField(STRING, "VARIANT", getCurrentVariant(), const = true)
    }
}

kotlin {
    sourceSets {
        androidMain.dependencies {
            implementation(libs.androidx.credentials)
            implementation(libs.androidx.credentials.compat)
        }
        commonMain.dependencies {
            implementation(projects.core)
            implementation(projects.core.domain)
            implementation(projects.core.data)
            implementation(projects.database)
            implementation(projects.feature.authorization.domain.api)
            implementation(projects.feature.authorization.data.source)
            implementation(projects.library.cache.api)
            implementation(libs.ktor.client.resources)
            implementation(libs.ktor.client.auth)
        }
    }
}