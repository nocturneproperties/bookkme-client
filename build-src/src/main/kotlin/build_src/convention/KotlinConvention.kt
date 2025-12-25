package build_src.convention

import build_src.tools.libs
import org.gradle.api.Project
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.dsl.KotlinAndroidProjectExtension
import org.jetbrains.kotlin.gradle.dsl.KotlinJvmProjectExtension
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension

internal fun KotlinJvmProjectExtension.applyConvention() {
    jvmToolchain(21)
}

internal fun KotlinAndroidProjectExtension.applyConvention() {
    jvmToolchain(21)
}

internal fun KotlinMultiplatformExtension.applyConvention(project: Project) {
    androidTarget {
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_21)
        }
    }

    jvmToolchain(21)
    compilerOptions {
        freeCompilerArgs.add("-Xexpect-actual-classes")
    }

    iosX64()
    iosArm64()
    iosSimulatorArm64()

    sourceSets.all {
        languageSettings.optIn("kotlinx.serialization.ExperimentalSerializationApi")
        languageSettings.optIn("kotlin.uuid.ExperimentalUuidApi")
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
    }
}