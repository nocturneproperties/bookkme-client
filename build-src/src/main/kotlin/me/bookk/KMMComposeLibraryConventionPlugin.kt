package me.bookk

import com.android.build.gradle.LibraryExtension
import com.codingfeline.buildkonfig.gradle.BuildKonfigExtension
import me.bookk.build_src.convention.applyConvention
import me.bookk.build_src.convention.applyFlavourConvention
import me.bookk.build_src.tools.libs
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
                apply(libs.plugins.buldconfig.get().pluginId)
            }

            extensions.getByType<LibraryExtension>().apply {
                applyConvention(target, useCompose = true)
                applyFlavourConvention()
            }
            extensions.getByType<BuildKonfigExtension>().applyConvention(target)
            extensions.getByType<KotlinMultiplatformExtension>().apply {
                applyConvention(target)

                sourceSets.androidMain.dependencies {
                    implementation(libs.androidx.core)
                    implementation(libs.compose.coil)
                    implementation(libs.compose.ui)
                    implementation(libs.compose.ui.tooling)
                    implementation(libs.compose.ui.tooling.preview)
                    implementation(libs.compose.material3)
                    implementation(libs.compose.navigation)
                    implementation(libs.androidx.activity.compose)
                    implementation(libs.kotlinx.coroutines.android)
                    implementation(libs.androidx.lifecycle.viewmodel)
                    implementation(libs.kmm.resources.compose)
                    implementation(libs.koin.android)
                    implementation(libs.koin.android.compose)
                }
                sourceSets.commonMain.dependencies {
                    implementation(libs.kmm.resources)
                    implementation(libs.compose.runtime)
                    implementation(libs.koin.core)
                }
            }

            dependencies {
                add("implementation", platform(libs.compose.bom))
            }
        }
    }
}