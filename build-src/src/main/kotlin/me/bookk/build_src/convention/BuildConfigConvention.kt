package me.bookk.build_src.convention

import com.android.build.gradle.LibraryExtension
import com.codingfeline.buildkonfig.compiler.FieldSpec.Type.BOOLEAN
import com.codingfeline.buildkonfig.compiler.FieldSpec.Type.STRING
import com.codingfeline.buildkonfig.gradle.BuildKonfigExtension
import com.codingfeline.buildkonfig.gradle.TargetConfigDsl
import me.bookk.build_src.constants.ProductFlavour
import me.bookk.build_src.tools.getCurrentVariant
import org.gradle.api.Action
import org.gradle.api.Project
import org.gradle.kotlin.dsl.getByType

fun BuildKonfigExtension.applyConvention(target: Project) {
    val extender = target.extensions.create(
        BuildKonfigExtendExtension.NAME,
        BuildKonfigExtendExtension::class.java,
        target
    )
    target.afterEvaluate {
        val androidExtension = target.extensions.getByType<LibraryExtension>()
        packageName = androidExtension.namespace
        androidExtension.libraryVariants.forEach { variant ->
            defaultConfigs(variant.name) {
                buildConfigField(
                    BOOLEAN,
                    "DEBUG",
                    variant.name.contains("debug", ignoreCase = true).toString(),
                    const = true
                )
                buildConfigField(STRING, "VARIANT", variant.name, const = true)

                extender.extenders
                    .filter { variant.name.contains(it.key.title) }
                    .values
                    .forEach { action ->
                        action.execute(this)
                    }
            }
        }
        target.setProperty("buildkonfig.flavor", getCurrentVariant())
    }
    defaultConfigs {

    }
}

open class BuildKonfigExtendExtension(private val project: Project) {

    internal val extenders = mutableMapOf<ProductFlavour, Action<TargetConfigDsl>>()

    fun forFlavour(pattern: ProductFlavour, action: Action<TargetConfigDsl>) {
        extenders[pattern] = action
    }

    companion object {
        internal const val NAME = "buildkonfigExtend"
    }
}

