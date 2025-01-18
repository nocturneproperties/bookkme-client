package me.bookk.build_src.convention

import com.android.build.gradle.LibraryExtension
import me.bookk.build_src.constants.AndroidConfig
import org.gradle.api.JavaVersion
import org.gradle.api.Project
import java.io.File

internal fun LibraryExtension.applyConvention(project: Project, useCompose: Boolean) {
    compileSdk = AndroidConfig.COMPILE_SDK
    defaultConfig {
        minSdk = AndroidConfig.MIN_SDK

        consumerProguardFiles.add(File(project.projectDir, "consumer-rules.pro"))
    }
    buildFeatures {
        compose = useCompose
    }
    if (useCompose) {
        project.applyComposeCompilerConvention()
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }
    buildTypes {
        getByName("debug") {
            isMinifyEnabled = false
            isShrinkResources = false
        }
        getByName("release") {
            isMinifyEnabled = false
            isShrinkResources = false
        }
    }
}