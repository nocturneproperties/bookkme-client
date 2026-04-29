package build_src.tools

import com.android.build.api.dsl.ApplicationBuildType
import com.google.firebase.crashlytics.buildtools.gradle.CrashlyticsExtension
import org.gradle.accessors.dm.LibrariesForLibs
import org.gradle.api.GradleException
import org.gradle.api.Project
import org.gradle.api.plugins.ExtensionAware
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.the
import java.util.Locale
import java.util.Properties

fun ApplicationBuildType.firebaseCrashlytics(configuration: CrashlyticsExtension.() -> Unit) {
    (this as ExtensionAware).configure(configuration)
}

fun Project.isIosBuild(): Boolean {
    return gradle.startParameter.taskNames.any { it.contains("ForXcode") }
}

fun Project.getCurrentVariant(): String {
    return run {
            val tskReqStr = gradle.startParameter.taskNames.toString()
            val patternStr = when {
                tskReqStr.contains("test") -> "(?<=test)\\w*?(?=UnitTest)"
                tskReqStr.contains("bundle") -> "(?<=bundle)\\w*?(?=Aar)"
                tskReqStr.contains("assemble") -> "(?<=assemble)\\w*"
                else -> "(?<=check)\\w*?(?=Manifest)"
            }
            Regex(patternStr).find(tskReqStr)?.value.orEmpty()
        }
        .ifBlank { properties["buildkonfig.flavor"]?.toString().orEmpty() }
        .replaceFirstChar { it.lowercase(Locale.getDefault()) }
}

fun Project.findStringProperty(key: String, fileName: String): String {
    val propertiesFile = project.rootProject.file(fileName)
    val properties = Properties()
    properties.load(propertiesFile.inputStream())
    return properties.getProperty(key) ?: throw GradleException("$key not found in $fileName")
}

val Project.libs: LibrariesForLibs
    get() = the<LibrariesForLibs>()