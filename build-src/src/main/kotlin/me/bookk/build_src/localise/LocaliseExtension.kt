package me.bookk.build_src.localise

import org.gradle.api.Action
import org.gradle.api.Project

open class LocaliseExtension(private val project: Project) {

    internal val configs = mutableListOf<LocaliseConfig>()

    fun config(configure: Action<LocaliseConfig>) {
        val config = LocaliseConfig()
        project.configure(listOf(config), configure)
        configs.add(config)
    }

    companion object {
        internal const val NAME = "localise"
    }
}