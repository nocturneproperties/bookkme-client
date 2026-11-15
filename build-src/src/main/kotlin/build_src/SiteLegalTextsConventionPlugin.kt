package build_src

import build_src.site.GenerateLegalTextsTask
import org.gradle.api.Plugin
import org.gradle.api.Project

class SiteLegalTextsConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        target.tasks.register("generateLegalTexts", GenerateLegalTextsTask::class.java) {
            group = "build"
            description =
                "Verifies the published legal texts are unchanged and compiles them into the site"
            legalDirectory.set(target.layout.projectDirectory.dir("legal"))
            outputDirectory.set(target.layout.buildDirectory.dir("generated/legalTexts/kotlin"))
        }
    }
}
