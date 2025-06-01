package build_src

import build_src.convention.applyConvention
import build_src.tools.libs
import com.codingfeline.buildkonfig.gradle.BuildKonfigExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.plugins.JavaPluginExtension
import org.gradle.kotlin.dsl.getByType
import org.jetbrains.kotlin.gradle.dsl.KotlinJvmProjectExtension

@Suppress("unused")
class KotlinLibraryConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            pluginManager.apply {
                apply(libs.plugins.kotlin.multiplatform.get().pluginId)
                apply(libs.plugins.buldconfig.get().pluginId)
            }

            extensions.getByType<JavaPluginExtension>().applyConvention()
            extensions.getByType<KotlinJvmProjectExtension>().applyConvention()
            extensions.getByType<BuildKonfigExtension>().applyConvention(target)
        }
    }
}