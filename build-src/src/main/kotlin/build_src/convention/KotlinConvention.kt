package build_src.convention

import build_src.constants.ApplicationConfig
import build_src.tools.isIosBuild
import build_src.tools.libs
import com.android.build.api.dsl.KotlinMultiplatformAndroidLibraryTarget
import org.gradle.api.Project
import org.gradle.api.file.DuplicatesStrategy
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.jetbrains.kotlin.gradle.plugin.KotlinCompilation
import org.jetbrains.kotlin.gradle.targets.js.ir.JsIrBinary
import org.jetbrains.kotlin.gradle.targets.js.ir.KotlinJsIrTarget

private val modulesWithAndroidResourcesInHostTests =
    setOf(":designsystem", ":shared:common", ":library:picker")

private fun Project.hasAndroidResourcesInHostTests(): Boolean {
    return path in modulesWithAndroidResourcesInHostTests || path.endsWith(":presentation")
}

private val modulesWithWebTarget = setOf(
    ":core",
    ":core:domain",
    ":core:data",
    ":core:testFixtures",
    ":core:presentation",
    ":designsystem",
    ":designsystem:testFixtures",
    ":library:money",
    ":library:device:api",
    ":library:device:impl",
    ":library:credentials:api",
    ":library:crashreporting:api",
    ":library:cache:api",
    ":library:cache:impl",
    ":library:files:api",
    ":library:files:impl",
    ":library:validation:api",
    ":database:schema",
    ":database:client",
    ":feature:authorization:data",
    ":feature:appointments:data",
    ":feature:business:data",
    ":feature:settings:data",
    ":feature:authorization:presentation",
    ":feature:dashboard:presentation",
    ":shared:common",
)

private val layersWithWebTarget = listOf(":domain:api", ":domain:impl", ":data:source")

private fun Project.hasWebTarget(): Boolean {
    return path in modulesWithWebTarget || layersWithWebTarget.any { path.endsWith(it) }
}

internal fun KotlinMultiplatformExtension.applyCompilerOptions() {
    compilerOptions {
        freeCompilerArgs.add("-Xexpect-actual-classes")
    }
}

internal fun KotlinMultiplatformExtension.skipDuplicateJsTestResources() {
    targets.withType(KotlinJsIrTarget::class.java).configureEach {
        compilations
            .matching { it.name == KotlinCompilation.TEST_COMPILATION_NAME }
            .configureEach {
                binaries.withType(JsIrBinary::class.java).configureEach {
                    linkSyncTask.configure { duplicatesStrategy = DuplicatesStrategy.EXCLUDE }
                }
            }
    }
}

internal fun KotlinMultiplatformExtension.applyConvention(project: Project) {
    targets.withType(KotlinMultiplatformAndroidLibraryTarget::class.java).configureEach {
        compileSdk = ApplicationConfig.COMPILE_SDK
        minSdk = ApplicationConfig.MIN_SDK
        androidResources { enable = true }
        withHostTest { isIncludeAndroidResources = project.hasAndroidResourcesInHostTests() }
    }

    jvmToolchain(21)
    applyCompilerOptions()

    iosArm64()
    iosSimulatorArm64()

    if (project.hasWebTarget()) {
        js {
            browser {
                testTask { enabled = false }
            }
            nodejs {
                testTask { useMocha { timeout = "10s" } }
            }
        }
        skipDuplicateJsTestResources()
    }

    sourceSets.all {
        if (project.path.endsWith("data") || project.path.endsWith("presentation")) {
            languageSettings.optIn("kotlinx.serialization.ExperimentalSerializationApi")
        }
        if (project.isIosBuild()) {
            languageSettings.optIn("kotlinx.cinterop.ExperimentalForeignApi")
        } else {
            if (project.path.endsWith("designsystem") || project.path.endsWith("presentation")) {
                languageSettings.optIn("androidx.compose.material3.ExperimentalMaterial3Api")
            }
        }
        languageSettings.optIn("kotlin.uuid.ExperimentalUuidApi")
        languageSettings.optIn("kotlinx.coroutines.FlowPreview")
        languageSettings.optIn("kotlinx.coroutines.ExperimentalCoroutinesApi")
    }
    sourceSets.androidMain.dependencies {
        implementation(project.libs.kotlinx.coroutines.android)
    }
    sourceSets.commonMain.dependencies {
        implementation(project.libs.koin.core)
        implementation(project.libs.kotlinx.datetime)
        implementation(project.libs.kotlinx.coroutines.core)
        implementation(project.libs.okio)
    }
    sourceSets.commonTest.dependencies {
        implementation(project.libs.test.kotlin)
        implementation(project.libs.test.koin)
        implementation(project.libs.kotlinx.coroutines.test)
    }
}
