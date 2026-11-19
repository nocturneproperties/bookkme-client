package build_src.site

import org.gradle.api.DefaultTask
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.CacheableTask
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction

@CacheableTask
abstract class GenerateWebManifestTask : DefaultTask() {
    @get:Input
    abstract val manifestName: Property<String>

    @get:Input
    abstract val manifestDescription: Property<String>

    @get:Input
    abstract val themeColor: Property<String>

    @get:Input
    abstract val backgroundColor: Property<String>

    @get:OutputDirectory
    abstract val outputDirectory: DirectoryProperty

    @TaskAction
    fun generate() {
        val output = outputDirectory.get().asFile
        output.deleteRecursively()
        val file = output.resolve("public/${WebManifest.FILE_NAME}")
        file.parentFile.mkdirs()
        file.writeText(
            WebManifest.json(
                name = manifestName.get(),
                description = manifestDescription.get(),
                themeColor = themeColor.get(),
                backgroundColor = backgroundColor.get(),
            ),
        )
    }
}
