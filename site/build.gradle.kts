import com.varabyte.kobweb.gradle.application.util.configAsKobwebApplication

plugins {
    id(libs.plugins.convention.kmm.web.get().pluginId)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.kobweb.application)
    alias(libs.plugins.kmm.resources)
    id(libs.plugins.convention.site.legal.get().pluginId)
}

group = "me.bookk.site"
version = "0.1"

kobweb {
    app {
        index {
            description.set("BookkMe — a booking app for small businesses on Android and iOS.")
            faviconPath.set("/favicon.svg")
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
