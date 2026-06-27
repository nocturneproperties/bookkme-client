package build_src

import build_src.convention.applyConvention
import build_src.tools.libs
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.getByType
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension

@Suppress("unused")
class KMMLibraryConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            pluginManager.apply {
                apply(libs.plugins.android.library.get().pluginId)
                apply(libs.plugins.kotlin.multiplatform.get().pluginId)
                apply(libs.plugins.mockery.get().pluginId)
            }

            extensions.getByType<KotlinMultiplatformExtension>().applyConvention(target)
        }
    }
}