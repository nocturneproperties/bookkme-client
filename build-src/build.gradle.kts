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
    compileOnly(libs.buildkonfig.compiler)
    compileOnly(libs.compose.compiler.gradlePlugin)
    compileOnly(libs.room.gradlePlugin)
    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
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
        register("kmmDatabase") {
            id = "convention.kmm.library.database"
            implementationClass = "build_src.KMMDatabaseConventionPlugin"
            version = "1.0"
        }
        register("kmmWeb") {
            id = "convention.kmm.web"
            implementationClass = "build_src.KMMWebConventionPlugin"
            version = "1.0"
        }
        register("siteLegalTexts") {
            id = "convention.site.legal"
            implementationClass = "build_src.SiteLegalTextsConventionPlugin"
            version = "1.0"
        }
        register("siteSecurityTxt") {
            id = "convention.site.securitytxt"
            implementationClass = "build_src.SiteSecurityTxtConventionPlugin"
            version = "1.0"
        }
        register("siteWebManifest") {
            id = "convention.site.webmanifest"
            implementationClass = "build_src.SiteWebManifestConventionPlugin"
            version = "1.0"
        }
        register("siteSitemap") {
            id = "convention.site.sitemap"
            implementationClass = "build_src.SiteSitemapConventionPlugin"
            version = "1.0"
        }
        register("siteBuildConfig") {
            id = "convention.site.buildconfig"
            implementationClass = "build_src.SiteBuildConfigConventionPlugin"
            version = "1.0"
        }
    }
}
