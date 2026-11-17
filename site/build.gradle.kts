import build_src.site.DesignSystemColors
import build_src.site.GenerateWebManifestTask
import build_src.site.WebManifest
import com.varabyte.kobweb.gradle.application.util.configAsKobwebApplication
import kotlinx.html.link
import kotlinx.html.meta

plugins {
    id(libs.plugins.convention.kmm.web.get().pluginId)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.kobweb.application)
    alias(libs.plugins.kmm.resources)
    id(libs.plugins.convention.site.legal.get().pluginId)
    id(libs.plugins.convention.site.securitytxt.get().pluginId)
    id(libs.plugins.convention.site.webmanifest.get().pluginId)
}

group = "me.bookk.site"
version = "0.1"

val siteName = "BookkMe"
val siteDescription = "BookkMe — a booking app for small businesses on Android and iOS."
val designSystemColors = DesignSystemColors.read(rootDir)

val generateWebManifest = tasks.named<GenerateWebManifestTask>("generateWebManifest") {
    manifestName.set(siteName)
    manifestDescription.set(siteDescription)
    themeColor.set(designSystemColors.opaque("lightBackground"))
    backgroundColor.set(designSystemColors.opaque("lightBackground"))
}

kobweb {
    app {
        index {
            val lightBackground = designSystemColors.opaque("lightBackground")
            val darkBackground = designSystemColors.opaque("darkBackground")
            description.set(siteDescription)
            faviconPath.set("")
            head.add {
                link(rel = "icon", href = "/favicon.ico") { attributes["sizes"] = "32x32" }
                link(rel = "icon", href = "/favicon.svg", type = "image/svg+xml")
                link(rel = "apple-touch-icon", href = "/apple-touch-icon.png")
                link(rel = "manifest", href = "/${WebManifest.FILE_NAME}")
                meta(name = "theme-color", content = lightBackground) {
                    attributes["media"] = "(prefers-color-scheme: light)"
                }
                meta(name = "theme-color", content = darkBackground) {
                    attributes["media"] = "(prefers-color-scheme: dark)"
                }
            }
        }
        export {
            suppressLayoutWarning.set(true)
            val localizedLanguages = listOf("uk", "pl")
            val localizedRoutes = listOf("", "/contact", "/privacy", "/terms", "/dpa")
            localizedLanguages.forEach { language ->
                localizedRoutes.forEach { route -> addExtraRoute("/$language$route") }
            }
        }
    }
}

val sqliteWasmResources = layout.buildDirectory.dir("generated/sqliteWasm")

val copySqliteWasm = tasks.register<Copy>("copySqliteWasm") {
    group = "build"
    description =
        "Copies the SQLite WASM runtime from npm into the site's public/sqlite resources for the Room web worker"
    dependsOn(":kotlinNpmInstall")
    from(rootProject.layout.buildDirectory.dir("js/node_modules/@sqlite.org/sqlite-wasm/dist")) {
        include("index.mjs", "sqlite3.wasm", "sqlite3-opfs-async-proxy.js")
        rename("index.mjs", "sqlite3.js")
    }
    into(sqliteWasmResources.map { it.dir("public/sqlite") })
}

kotlin {
    configAsKobwebApplication("site")
    js {
        nodejs {
            testTask { useMocha { timeout = "10s" } }
        }
    }

    sourceSets {
        jsMain {
            resources.srcDir(copySqliteWasm.map { sqliteWasmResources.get() })
            resources.srcDir(generateWebManifest)
            kotlin.srcDir(tasks.named("generateLegalTexts"))
        }
        jsMain.dependencies {
            implementation(npm("@sqlite.org/sqlite-wasm", libs.versions.npm.sqlite.wasm.get()))
            implementation(projects.site.designsystem)
            implementation(projects.site.domcompat)
            implementation(libs.kmm.resources)
            implementation(libs.kobweb.compose.runtime)
            implementation(libs.compose.html.core)
            implementation(libs.kobweb.core)
            implementation(libs.kobweb.silk)
            implementation(libs.kotlinx.datetime)
        }
        jsTest.dependencies {
            implementation(projects.core.testFixtures)
            implementation(libs.test.kotlin)
            implementation(libs.kotlinx.coroutines.test)
        }
    }
}

multiplatformResources {
    resourcesPackage.set("me.bookk.site.resources")
    resourcesClassName.set("SiteRes")
}

configurations.configureEach {
    exclude(group = "org.jetbrains.kotlin", module = "kotlin-dom-api-compat")
}

tasks.withType<org.jetbrains.kotlin.gradle.targets.js.ir.KotlinJsIrLink>().configureEach {
    compilerOptions.freeCompilerArgs.add("-Xpartial-linkage-loglevel=ERROR")
}
