package build_src.convention

import build_src.constants.ApplicationConfig
import build_src.constants.ProductFlavour
import build_src.tools.getCurrentVariant
import com.android.build.gradle.LibraryExtension
import com.codingfeline.buildkonfig.compiler.FieldSpec.Type.BOOLEAN
import com.codingfeline.buildkonfig.compiler.FieldSpec.Type.INT
import com.codingfeline.buildkonfig.compiler.FieldSpec.Type.STRING
import com.codingfeline.buildkonfig.gradle.BuildKonfigExtension
import org.gradle.api.Project
import org.gradle.kotlin.dsl.getByType

fun BuildKonfigExtension.applyConvention(target: Project) {
    target.afterEvaluate {
        val androidExtension = target.extensions.getByType<LibraryExtension>()
        packageName = androidExtension.namespace
        val variant = getCurrentVariant()
        defaultConfigs {
            buildConfigField(
                BOOLEAN,
                "DEBUG",
                variant.contains("debug", ignoreCase = true).toString(),
                const = true
            )
            buildConfigField(STRING, "VARIANT", getCurrentVariant(), const = true)
            buildConfigField(STRING, "FLAVOUR", ProductFlavour.from(variant).title, const = true)
            buildConfigField(
                STRING,
                "VERSION_NAME",
                ApplicationConfig.VERSION_NAME,
                const = true
            )
            buildConfigField(
                INT,
                "VERSION_CODE",
                ApplicationConfig.VERSION_CODE.toString(),
                const = true
            )
        }
    }
}

