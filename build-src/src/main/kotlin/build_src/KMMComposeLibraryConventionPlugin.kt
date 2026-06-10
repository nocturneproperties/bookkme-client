package build_src

import build_src.convention.applyConvention
import build_src.tools.libs
import com.android.build.gradle.LibraryExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.getByType
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension

@Suppress("unused")
class KMMComposeLibraryConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            pluginManager.apply {
                apply(libs.plugins.android.library.get().pluginId)
                apply(libs.plugins.kotlin.multiplatform.get().pluginId)
                apply(libs.plugins.compose.compiler.get().pluginId)
                apply(libs.plugins.kmm.resources.get().pluginId)
                apply(libs.plugins.kotlin.serialization.get().pluginId)
            }

            extensions.getByType<LibraryExtension>().apply {
                applyConvention(target, useCompose = true)
            }
            extensions.getByType<KotlinMultiplatformExtension>().apply {
                applyConvention(target)

                sourceSets.androidMain.dependencies {
                    implementation(libs.androidx.core)
                    implementation(libs.compose.coil)
                    implementation(libs.compose.coil.svg)
                    implementation(libs.compose.coil.network.okhttp)
                    implementation(libs.compose.ui)
                    implementation(libs.compose.material3)
                    implementation(libs.compose.navigation)
                    implementation(libs.compose.icons.extended)
                    implementation(libs.androidx.activity.compose)
                    implementation(libs.kotlinx.coroutines.android)
                    implementation(libs.androidx.lifecycle.viewmodel)
                    implementation(libs.kmm.resources.compose)
                    implementation(libs.koin.android)
                    implementation(libs.koin.android.compose)
                }
                sourceSets.commonMain.dependencies {
                    implementation(libs.kotlin.serialization)
                    implementation(libs.kmm.resources)
                    implementation(libs.compose.runtime)
                    implementation(libs.koin.core)
                    implementation(libs.koin.annotation)
                }
            }

            dependencies {
                add("debugImplementation", libs.compose.ui.tooling)
                add("implementation", libs.compose.ui.tooling.preview)
                add("implementation", platform(libs.compose.bom))
            }
        }
    }
}