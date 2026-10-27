package build_src.convention

import org.gradle.api.Project
import org.jetbrains.kotlin.compose.compiler.gradle.ComposeCompilerGradlePluginExtension
import org.jetbrains.kotlin.gradle.plugin.KotlinPlatformType

fun ComposeCompilerGradlePluginExtension.applyConvention(project: Project) {
    targetKotlinPlatforms.set(setOf(KotlinPlatformType.androidJvm))
    stabilityConfigurationFiles.add(
        project.rootProject.layout.projectDirectory.file("build-src/compose-stability-config.txt")
    )
//    Uncomment to generate reports
//    reportsDestination.set(
//        project.layout.buildDirectory.get().dir("compose_reports")
//    )
//    metricsDestination.set(
//        project.layout.buildDirectory.get().dir("compose_reports")
//    )
}