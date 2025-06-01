package build_src

import build_src.convention.applyConvention
import build_src.convention.applyFlavourConvention
import build_src.tools.libs
import com.android.build.api.dsl.ApplicationExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.getByType
import org.jetbrains.kotlin.gradle.dsl.KotlinAndroidProjectExtension

@Suppress("unused")
class AndroidApplicationConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            pluginManager.apply {
                apply(libs.plugins.android.application.get().pluginId)
                apply(libs.plugins.kotlin.android.get().pluginId)
                apply(libs.plugins.compose.compiler.get().pluginId)
                apply(libs.plugins.google.services.get().pluginId)
                apply(libs.plugins.firebase.crashlytics.get().pluginId)
            }

            extensions.getByType<ApplicationExtension>().apply {
                applyConvention(target)
                applyFlavourConvention()
                extensions.getByType<KotlinAndroidProjectExtension>().applyConvention()
            }

            dependencies {
                add("implementation", libs.androidx.splash)
                add("implementation", libs.koin.core)
                add("implementation", libs.koin.android)
                add("implementation", libs.koin.android.compose)

                add("implementation", platform(libs.compose.bom))
                add("implementation", libs.androidx.core)
                add("implementation", libs.compose.ui)
                add("implementation", libs.compose.ui.tooling)
                add("implementation", libs.compose.ui.tooling.preview)
                add("implementation", libs.compose.material3)
                add("implementation", libs.compose.navigation)
                add("implementation", libs.androidx.activity.compose)

                add("implementation", platform(libs.firebase.bom))
                add("implementation", libs.firebase.analytics)
                add("implementation", libs.firebase.crashlytics)
            }
        }
    }
}