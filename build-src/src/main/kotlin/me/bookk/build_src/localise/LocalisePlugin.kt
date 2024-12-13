package me.bookk.build_src.localise

import org.gradle.api.Plugin
import org.gradle.api.Project

@Suppress("unused")
class LocalisePlugin : Plugin<Project> {
    override fun apply(target: Project) {
        target.extensions.create(LocaliseExtension.NAME, LocaliseExtension::class.java, target)
        target.tasks.create(LocaliseSyncTask.NAME, LocaliseSyncTask::class.java) {
            group = "localise"
        }
    }
}