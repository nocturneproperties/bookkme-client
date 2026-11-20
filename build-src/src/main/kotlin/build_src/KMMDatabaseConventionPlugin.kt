package build_src

import build_src.convention.applyConvention
import build_src.tools.libs
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.getByType
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension

@Suppress("unused")
class KMMDatabaseConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            pluginManager.apply {
                apply(libs.plugins.android.library.get().pluginId)
                apply(libs.plugins.kotlin.multiplatform.get().pluginId)
                apply(libs.plugins.android.room.get().pluginId)
                apply(libs.plugins.google.ksp.get().pluginId)
            }

            val kotlin = extensions.getByType<KotlinMultiplatformExtension>()
            kotlin.applyConvention(target)
            val hasJsTarget = kotlin.targets.findByName("js") != null
            kotlin.apply {
                sourceSets.commonMain.dependencies {
                    implementation(libs.room.runtime)
                    implementation(libs.room.sqlite.async)
                }
                sourceSets.androidMain.dependencies {
                    implementation(libs.room.sqlite)
                }
                sourceSets.iosMain.dependencies {
                    implementation(libs.room.sqlite)
                }
                if (hasJsTarget) {
                    sourceSets.jsMain.dependencies {
                        implementation(libs.room.sqlite.web)
                    }
                }
            }
            dependencies {
                add("kspAndroid", libs.room.compiler)
                add("kspIosSimulatorArm64", libs.room.compiler)
                add("kspIosArm64", libs.room.compiler)
                if (hasJsTarget) {
                    add("kspJs", libs.room.compiler)
                }
            }
        }
    }
}
