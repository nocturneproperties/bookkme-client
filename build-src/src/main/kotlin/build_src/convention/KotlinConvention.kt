package build_src.convention

import build_src.constants.ApplicationConfig
import build_src.tools.isIosBuild
import build_src.tools.libs
import com.android.build.api.dsl.KotlinMultiplatformAndroidLibraryTarget
import org.gradle.api.Project
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension

internal fun KotlinMultiplatformExtension.applyConvention(project: Project) {
    targets.withType(KotlinMultiplatformAndroidLibraryTarget::class.java).configureEach {
        compileSdk = ApplicationConfig.COMPILE_SDK
        minSdk = ApplicationConfig.MIN_SDK
        androidResources { enable = true }
        withHostTest {}
    }

    jvmToolchain(21)
    compilerOptions {
        freeCompilerArgs.add("-Xexpect-actual-classes")
    }

    iosX64()
    iosArm64()
    iosSimulatorArm64()

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
