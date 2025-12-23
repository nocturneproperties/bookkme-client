package build_src.convention

import build_src.constants.ApplicationConfig
import com.android.build.api.dsl.ApplicationExtension
import org.gradle.api.JavaVersion
import org.gradle.api.Project
import java.io.File

internal fun ApplicationExtension.applyConvention(project: Project) {
    compileSdk = ApplicationConfig.COMPILE_SDK
    defaultConfig {
        minSdk = ApplicationConfig.MIN_SDK
        targetSdk = ApplicationConfig.COMPILE_SDK
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }
    buildFeatures {
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