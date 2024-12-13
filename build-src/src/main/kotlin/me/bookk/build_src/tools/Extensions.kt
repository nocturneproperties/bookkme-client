package me.bookk.build_src.tools

import com.android.build.api.dsl.ApplicationBuildType
import com.google.firebase.crashlytics.buildtools.gradle.CrashlyticsExtension
import org.gradle.accessors.dm.LibrariesForLibs
import org.gradle.api.GradleException
import org.gradle.api.Project
import org.gradle.api.plugins.ExtensionAware
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.the
import java.util.Properties
import java.util.regex.Pattern

fun ApplicationBuildType.firebaseCrashlytics(configuration: CrashlyticsExtension.() -> Unit) {
    (this as ExtensionAware).configure(configuration)
}

fun Project.getCurrentFlavor(): String {
    val tskReqStr = gradle.startParameter.taskRequests.toString()

    val patternStr = when {
        tskReqStr.contains("assemble") -> "assemble(\\w+)(Release|Debug)"
        tskReqStr.contains("bundle") -> "bundle(\\w+)(Release|Debug)"
        else -> "generate(\\w+)(Release|Debug)"
    }

    val pattern = Pattern.compile(patternStr)
    val matcher = pattern.matcher(tskReqStr)
    return if (matcher.find())
        matcher.group(1).lowercase()
    else {
        ""
    }
}

fun Project.findStringProperty(key: String, fileName: String): String {
    val propertiesFile = project.rootProject.file(fileName)
    val properties = Properties()
    properties.load(propertiesFile.inputStream())
    return properties.getProperty(key)?.toString()
        ?: throw GradleException("$key not found in $fileName")
}

val Project.libs: LibrariesForLibs
    get() = the<LibrariesForLibs>()