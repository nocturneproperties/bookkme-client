import com.varabyte.kobweb.gradle.application.util.configAsKobwebApplication

plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.kobweb.application)
    alias(libs.plugins.kmm.resources)
}

group = "me.bookk.site"
version = "0.1"

kobweb {
    app {
        index {
            description.set("BookkMe — a booking app for small businesses on Android and iOS.")
            faviconPath.set("/favicon.svg")
        }
    }
}

val sqliteWasmResources = layout.buildDirectory.dir("generated/sqliteWasm")

val copySqliteWasm by tasks.registering(Copy::class) {
    dependsOn(":kotlinNpmInstall")
    from(rootProject.layout.buildDirectory.dir("js/node_modules/@sqlite.org/sqlite-wasm/dist")) {
        include("index.mjs", "sqlite3.wasm", "sqlite3-opfs-async-proxy.js")
        rename("index.mjs", "sqlite3.js")
    }
    into(sqliteWasmResources.map { it.dir("public/sqlite") })
}

kotlin {
    configAsKobwebApplication("site")

    sourceSets {
        jsMain {
            resources.srcDir(copySqliteWasm.map { sqliteWasmResources.get() })
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
