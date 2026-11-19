package build_src

import build_src.site.GenerateWebManifestTask
import org.gradle.api.Plugin
import org.gradle.api.Project

class SiteWebManifestConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        target.tasks.register("generateWebManifest", GenerateWebManifestTask::class.java) {
            group = "build"
            description = "Generates the site's web app manifest with the design system colors"
            outputDirectory.set(target.layout.buildDirectory.dir("generated/webManifest"))
        }
    }
}
