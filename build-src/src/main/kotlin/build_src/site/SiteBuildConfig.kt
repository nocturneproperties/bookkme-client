package build_src.site

import org.gradle.api.DefaultTask
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.provider.ListProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.CacheableTask
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction

object SiteBuildConfig {
    fun kotlin(origin: String, pagePaths: List<String>, languages: List<String>): String {
        return buildString {
            appendLine("package me.bookk.site.config")
            appendLine()
            appendLine("object SiteBuildConfig {")
            appendLine("    const val ORIGIN = ${origin.kotlinLiteral()}")
            appendLine("    val pagePaths: List<String> = ${pagePaths.kotlinListOf()}")
            appendLine("    val languages: List<String> = ${languages.kotlinListOf()}")
            appendLine("}")
        }
    }
}

@CacheableTask
abstract class GenerateSiteBuildConfigTask : DefaultTask() {
    @get:Input
    abstract val origin: Property<String>

    @get:Input
    abstract val pagePaths: ListProperty<String>

    @get:Input
    abstract val languages: ListProperty<String>

    @get:OutputDirectory
    abstract val outputDirectory: DirectoryProperty

    @TaskAction
    fun generate() {
        val output = outputDirectory.get().asFile
        output.deleteRecursively()
        val file = output.resolve("me/bookk/site/config/SiteBuildConfig.kt")
        file.parentFile.mkdirs()
        file.writeText(SiteBuildConfig.kotlin(origin.get(), pagePaths.get(), languages.get()))
    }
}
