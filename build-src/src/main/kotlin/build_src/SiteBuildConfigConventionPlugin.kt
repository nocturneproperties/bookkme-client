package build_src

import build_src.site.GenerateSiteBuildConfigTask
import org.gradle.api.Plugin
import org.gradle.api.Project

class SiteBuildConfigConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        target.tasks.register("generateSiteBuildConfig", GenerateSiteBuildConfigTask::class.java) {
            group = "build"
            description =
                "Compiles the site's origin, page paths and languages from the build script into the site"
            outputDirectory.set(
                target.layout.buildDirectory.dir("generated/siteBuildConfig/kotlin"),
            )
        }
    }
}
