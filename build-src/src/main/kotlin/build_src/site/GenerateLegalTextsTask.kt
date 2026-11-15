package build_src.site

import org.gradle.api.DefaultTask
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.tasks.CacheableTask
import org.gradle.api.tasks.InputDirectory
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.PathSensitive
import org.gradle.api.tasks.PathSensitivity
import org.gradle.api.tasks.TaskAction

@CacheableTask
abstract class GenerateLegalTextsTask : DefaultTask() {
    @get:InputDirectory
    @get:PathSensitive(PathSensitivity.RELATIVE)
    abstract val legalDirectory: DirectoryProperty

    @get:OutputDirectory
    abstract val outputDirectory: DirectoryProperty

    @TaskAction
    fun generate() {
        val texts = LegalTextsReader(legalDirectory.get().asFile).read()
        val output = outputDirectory.get().asFile
        output.deleteRecursively()
        val file = output.resolve("me/bookk/site/legal/PublishedLegalTexts.kt")
        file.parentFile.mkdirs()
        file.writeText(LegalTextsWriter.write(texts))
    }
}
