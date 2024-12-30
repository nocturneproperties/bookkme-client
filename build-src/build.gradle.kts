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
            id = "me.bookk.android.application"
            implementationClass = "me.bookk.AndroidApplicationConventionPlugin"
            version = "1.0"
        }
        register("kmmLibrary") {
            id = "me.bookk.kmm.library"
            implementationClass = "me.bookk.KMMLibraryConventionPlugin"
            version = "1.0"
        }
        register("kmmComposeLibrary") {
            id = "me.bookk.kmm.library.compose"
            implementationClass = "me.bookk.KMMComposeLibraryConventionPlugin"
            version = "1.0"
        }
        register("kotlinLibrary") {
            id = "me.bookk.kotlin.library"
            implementationClass = "me.bookk.KotlinLibraryConventionPlugin"
            version = "1.0"
        }
        register("kmmDatabase") {
            id = "me.bookk.kmm.library.database"
            implementationClass = "me.bookk.KMMDatabaseConventionPlugin"
            version = "1.0"
        }
    }
}