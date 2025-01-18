package me.bookk

import com.android.build.gradle.LibraryExtension
import com.codingfeline.buildkonfig.gradle.BuildKonfigExtension
import me.bookk.build_src.convention.applyConvention
import me.bookk.build_src.convention.applyFlavourConvention
import me.bookk.build_src.tools.libs
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
                apply(libs.plugins.buldconfig.get().pluginId)
            }

            extensions.getByType<LibraryExtension>().apply {
                applyConvention(target, useCompose = false)
                applyFlavourConvention()
                extensions.getByType<KotlinMultiplatformExtension>().applyConvention(target)
            }
            extensions.getByType<BuildKonfigExtension>().applyConvention(target)
        }
    }
}