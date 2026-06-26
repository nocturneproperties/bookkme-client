package build_src

import build_src.convention.applyConvention
import build_src.convention.applyFlavourConvention
import build_src.tools.libs
import com.android.build.api.dsl.ApplicationExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.getByType

@Suppress("unused")
class AndroidApplicationConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            pluginManager.apply {
                apply(libs.plugins.android.application.get().pluginId)
                apply(libs.plugins.compose.compiler.get().pluginId)
                apply(libs.plugins.google.services.get().pluginId)
                apply(libs.plugins.firebase.crashlytics.get().pluginId)
                apply(libs.plugins.firebase.appDistribution.get().pluginId)
            }

            extensions.getByType<ApplicationExtension>().apply {
                applyConvention(target)
                applyFlavourConvention(target.projectDir)
            }
        }
    }
}