package build_src

import build_src.convention.applyConvention
import build_src.tools.libs
import com.android.build.gradle.LibraryExtension
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
            }

            extensions.getByType<LibraryExtension>().apply {
                applyConvention(target, useCompose = false)
                extensions.getByType<KotlinMultiplatformExtension>().applyConvention(target)
            }
        }
    }
}