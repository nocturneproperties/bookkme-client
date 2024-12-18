package me.bookk

import com.android.build.gradle.LibraryExtension
import me.bookk.build_src.convention.applyConvention
import me.bookk.build_src.convention.applyFlavourConvention
import me.bookk.build_src.tools.libs
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

            extensions.getByType<LibraryExtension>().apply {
                applyConvention(target, useCompose = false)
                applyFlavourConvention()
            }
            extensions.getByType<KotlinMultiplatformExtension>().apply {
                applyConvention(target)
                sourceSets.commonMain.dependencies {
                    implementation(libs.koin.core)
                    implementation(libs.room.runtime)
                    implementation(libs.room.sqlite)
                }
            }
            dependencies {
                add("kspAndroid", libs.room.compiler)
                add("kspIosSimulatorArm64", libs.room.compiler)
                add("kspIosX64", libs.room.compiler)
                add("kspIosArm64", libs.room.compiler)
            }
        }
    }
}