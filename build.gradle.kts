import com.diffplug.spotless.LineEnding

plugins {
    base
    alias(libs.plugins.spotless)
    // trick: for the same plugin versions in all sub-modules
    alias(libs.plugins.android.application).apply(false)
    alias(libs.plugins.android.library).apply(false)
    alias(libs.plugins.android.room).apply(false)
    alias(libs.plugins.kotlin.multiplatform).apply(false)
    alias(libs.plugins.kotlin.serialization).apply(false)
    alias(libs.plugins.compose.compiler).apply(false)
    alias(libs.plugins.mockery).apply(false)
    alias(libs.plugins.convention.kmm.library.kotlin).apply(false)
    alias(libs.plugins.convention.kmm.library.compose).apply(false)
    alias(libs.plugins.convention.kmm.database).apply(false)
    alias(libs.plugins.convention.android.application).apply(false)
    alias(libs.plugins.google.services).apply(false)
    alias(libs.plugins.google.ksp).apply(false)
    alias(libs.plugins.firebase.crashlytics).apply(false)
    alias(libs.plugins.firebase.appDistribution).apply(false)
    alias(libs.plugins.buldconfig).apply(false)
    alias(libs.plugins.kmm.resources).apply(false)
}

spotless {
    lineEndings = LineEnding.UNIX
    kotlin {
        target(
            fileTree(rootDir) {
                include("**/src/**/*.kt")
                exclude("**/build/**", "**/generated/**", "**/.gradle/**", "feature/.template/**")
            },
        )
        ktlint(libs.versions.ktlint.get())
            .editorConfigOverride(mapOf("ktlint_code_style" to "intellij_idea"))
        suppressLintsFor {
            path = "designsystem/src/iosMain/kotlin/me/bookk/designsystem/uistate/Info.kt"
            shortCode = "standard:kdoc"
        }
        suppressLintsFor {
            shortCode = "standard:filename"
        }
        suppressLintsFor {
            shortCode = "standard:package-name"
        }
        suppressLintsFor {
            shortCode = "standard:no-empty-file"
        }
    }
    kotlinGradle {
        target(
            fileTree(rootDir) {
                include("**/*.gradle.kts")
                exclude("**/build/**", "**/generated/**", "**/.gradle/**", "feature/.template/**")
            },
        )
        ktlint(libs.versions.ktlint.get())
            .editorConfigOverride(mapOf("ktlint_code_style" to "intellij_idea"))
    }
}

val flavor = run {
    val tskReqStr = gradle.startParameter.taskNames.toString()
    val patternStr =
        when {
            tskReqStr.contains("test") -> "(?<=test)\\w*?(?=UnitTest)"
            tskReqStr.contains("bundle") -> "(?<=bundle)\\w*?(?=Aar)"
            tskReqStr.contains("assemble") -> "(?<=assemble)\\w*"
            else -> "(?<=check)\\w*?(?=Manifest)"
        }

    Regex(patternStr).find(tskReqStr)?.value.orEmpty().replaceFirstChar { it.lowercase() }
}

allprojects {
    val prop = properties["buildkonfig.flavor"]
    if (flavor.isNotBlank() && (prop == null || prop == "any")) {
        setProperty("buildkonfig.flavor", flavor)
    }
}
