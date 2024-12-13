package me.bookk.build_src.convention

import com.android.build.api.dsl.ApplicationExtension
import me.bookk.build_src.constants.AndroidConfig
import org.gradle.api.JavaVersion
import org.gradle.api.Project
import java.io.File

internal fun ApplicationExtension.applyConvention(project: Project) {
    compileSdk = AndroidConfig.COMPILE_SDK
    defaultConfig {
        minSdk = AndroidConfig.MIN_SDK
        targetSdk = AndroidConfig.COMPILE_SDK
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }
    buildFeatures {
        buildConfig = true
        compose = true
    }
    project.applyComposeCompilerConvention()
    buildTypes {
        getByName("debug") {
            isMinifyEnabled = false
            isShrinkResources = false
        }
        getByName("release") {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles.addAll(
                arrayOf(
                    getDefaultProguardFile("proguard-android-optimize.txt"),
                    File(project.projectDir,"proguard-rules.pro")
                )
            )
        }
    }
}