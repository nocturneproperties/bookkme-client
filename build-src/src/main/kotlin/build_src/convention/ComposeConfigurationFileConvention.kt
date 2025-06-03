package build_src.convention

import org.gradle.api.Project
import org.gradle.kotlin.dsl.withType
import org.jetbrains.kotlin.gradle.tasks.KotlinJvmCompile

internal fun Project.applyComposeCompilerConvention() {
    tasks.withType<KotlinJvmCompile>().configureEach {
        compilerOptions {
            freeCompilerArgs.addAll(
                "-P",
                "plugin:androidx.compose.compiler.plugins.kotlin:stabilityConfigurationPath=${project.rootDir}/build-src/compose-stability-config.txt"
            )
        }
    }

    // Uncomment to generate reports
//    val reportDir = "compose_reports"
//    freeCompilerArgs += listOf(
//        "-P",
//        "plugin:androidx.compose.compiler.plugins.kotlin:reportsDestination=" +
//                project.layout.buildDirectory.get()
//                    .dir(reportDir).asFile.absolutePath,
//        "-P",
//        "plugin:androidx.compose.compiler.plugins.kotlin:metricsDestination=" +
//                project.layout.buildDirectory.get()
//                    .dir(reportDir).asFile.absolutePath,
//    )
}