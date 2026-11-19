package build_src.site

import org.gradle.api.DefaultTask
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.provider.ListProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.CacheableTask
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction

@CacheableTask
abstract class GenerateSitemapTask : DefaultTask() {
    @get:Input
    abstract val origin: Property<String>

    @get:Input
    abstract val pagePaths: ListProperty<String>

    @get:Input
    abstract val defaultLanguage: Property<String>

    @get:Input
    abstract val otherLanguages: ListProperty<String>

    @get:Input
    abstract val versionedPaths: ListProperty<String>

    @get:OutputDirectory
    abstract val outputDirectory: DirectoryProperty

    @TaskAction
    fun generate() {
        val pages = SitePages(
            pagePaths = pagePaths.get(),
            versionedPaths = versionedPaths.get(),
            defaultLanguage = defaultLanguage.get(),
            otherLanguages = otherLanguages.get(),
        )
        val output = outputDirectory.get().asFile
        output.deleteRecursively()
        val public = output.resolve("public")
        public.mkdirs()
        public.resolve(Sitemap.FILE_NAME).writeText(Sitemap.xml(origin.get(), pages))
        public.resolve(RobotsTxt.FILE_NAME).writeText(RobotsTxt.text(origin.get()))
    }
}
