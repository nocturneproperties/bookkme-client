plugins {
    `kotlin-dsl`
}

group = "me.bookk"

java {
    sourceCompatibility = JavaVersion.VERSION_21
    targetCompatibility = JavaVersion.VERSION_21
}

kotlin {
    jvmToolchain(21)
}

dependencies {
    compileOnly(files(libs.javaClass.superclass.protectionDomain.codeSource.location))
    compileOnly(libs.android.gradlePlugin)
    compileOnly(libs.kotlin.gradlePlugin)
    compileOnly(libs.kotlin.multiplatform.gradlePlugin)
    compileOnly(libs.firebase.crashlytics.gradlePlugin)
    compileOnly(libs.buildkonfig.gradlePlugin)
    compileOnly(libs.buildkonfig.compiler)
}

gradlePlugin {
    plugins {
        register("androidApp") {
            id = "convention.android.application"
            implementationClass = "build_src.AndroidApplicationConventionPlugin"
            version = "1.0"
        }
        register("kmmLibrary") {
            id = "convention.kmm.library"
            implementationClass = "build_src.KMMLibraryConventionPlugin"
            version = "1.0"
        }
        register("kmmComposeLibrary") {
            id = "convention.kmm.library.compose"
            implementationClass = "build_src.KMMComposeLibraryConventionPlugin"
            version = "1.0"
        }
        register("kotlinLibrary") {
            id = "convention.kotlin.library"
            implementationClass = "build_src.KotlinLibraryConventionPlugin"
            version = "1.0"
        }
        register("kmmDatabase") {
            id = "convention.kmm.library.database"
            implementationClass = "build_src.KMMDatabaseConventionPlugin"
            version = "1.0"
        }
    }
}