package build_src

import build_src.site.VerifySecurityTxtTask
import org.gradle.api.Plugin
import org.gradle.api.Project

class SiteSecurityTxtConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        val verify = target.tasks.register("verifySecurityTxt", VerifySecurityTxtTask::class.java) {
            group = "verification"
            description =
                "Fails when the security.txt Expires date is under 30 days or over a year away"
            securityTxt.set(
                target.layout.projectDirectory.file(
                    "src/jsMain/resources/public/.well-known/security.txt",
                ),
            )
        }
        target.tasks
            .matching { it.name == "jsProcessResources" || it.name == "check" }
            .configureEach { dependsOn(verify) }
    }
}
