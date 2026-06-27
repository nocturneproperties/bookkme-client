plugins {
    //trick: for the same plugin versions in all sub-modules
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
val flavor = run {
    val tskReqStr = gradle.startParameter.taskNames.toString()
    val patternStr = when {
        tskReqStr.contains("test") -> "(?<=test)\\w*?(?=UnitTest)"
        tskReqStr.contains("bundle") -> "(?<=bundle)\\w*?(?=Aar)"
        tskReqStr.contains("assemble") -> "(?<=assemble)\\w*"
        else -> "(?<=check)\\w*?(?=Manifest)"
    }

    Regex(patternStr).find(tskReqStr)?.value
        .orEmpty()
        .replaceFirstChar { it.lowercase() }
}

allprojects {
    val prop = properties["buildkonfig.flavor"]
    if (flavor.isNotBlank() && (prop == null || prop == "any")) {
        setProperty("buildkonfig.flavor", flavor)
    }
}
