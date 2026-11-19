package build_src

import build_src.site.GenerateSitemapTask
import org.gradle.api.Plugin
import org.gradle.api.Project

class SiteSitemapConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        target.tasks.register("generateSitemap", GenerateSitemapTask::class.java) {
            group = "build"
            description =
                "Generates the site's sitemap.xml and robots.txt from its pages and published legal texts"
            outputDirectory.set(target.layout.buildDirectory.dir("generated/sitemap"))
        }
    }
}
