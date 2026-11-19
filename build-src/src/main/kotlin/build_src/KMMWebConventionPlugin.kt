package build_src

import build_src.convention.applyCompilerOptions
import build_src.convention.skipDuplicateJsTestResources
import build_src.tools.libs
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.getByType
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension

@Suppress("unused")
class KMMWebConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            pluginManager.apply(libs.plugins.kotlin.multiplatform.get().pluginId)
            with(extensions.getByType<KotlinMultiplatformExtension>()) {
                applyCompilerOptions()
                skipDuplicateJsTestResources()
            }
        }
    }
}
