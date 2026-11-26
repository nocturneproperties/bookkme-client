import build_src.site.DesignSystemColors
import com.varabyte.kobweb.gradle.application.util.configAsKobwebApplication
import kotlinx.html.link
import kotlinx.html.meta

plugins {
    id(libs.plugins.convention.kmm.web.get().pluginId)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.kobweb.application)
    alias(libs.plugins.kmm.resources)
    alias(libs.plugins.mockery)
}

group = "me.bookk.client"
version = "0.1.0"

val designSystemColors = DesignSystemColors.read(rootDir)

kobweb {
    app {
        index {
            val lightBackground = designSystemColors.opaque("lightBackground")
            val darkBackground = designSystemColors.opaque("darkBackground")
            faviconPath.set("")
            head.add {
                link(rel = "icon", href = "/favicon.ico") { attributes["sizes"] = "32x32" }
                link(rel = "icon", href = "/favicon.svg", type = "image/svg+xml")
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
        }
    }
}

val sqliteWasmResources = layout.buildDirectory.dir("generated/sqliteWasm")

val copySqliteWasm = tasks.register<Copy>("copySqliteWasm") {
    group = "build"
    description =
        "Copies the SQLite WASM runtime from npm into the client app's public/sqlite resources for the Room web worker"
    dependsOn(":kotlinNpmInstall")
    from(rootProject.layout.buildDirectory.dir("js/node_modules/@sqlite.org/sqlite-wasm/dist")) {
        include("index.mjs", "sqlite3.wasm", "sqlite3-opfs-async-proxy.js")
        rename("index.mjs", "sqlite3.js")
    }
    into(sqliteWasmResources.map { it.dir("public/sqlite") })
}

kotlin {
    configAsKobwebApplication("client")
    js {
        nodejs {
            testTask { useMocha { timeout = "10s" } }
        }
    }

    sourceSets {
        jsMain {
            resources.srcDir(copySqliteWasm.map { sqliteWasmResources.get() })
        }
        jsMain.dependencies {
            implementation(npm("@sqlite.org/sqlite-wasm", libs.versions.npm.sqlite.wasm.get()))
            implementation(projects.designsystem)
            implementation(projects.site.domcompat)
            implementation(projects.library.telegram)
            implementation(projects.core)
            implementation(projects.shared.common)
            implementation(projects.library.device.impl)
            implementation(projects.library.crashreporting.api)
            implementation(projects.library.cache.impl)
            implementation(projects.library.files.api)
            implementation(projects.library.files.impl)
            implementation(projects.library.validation.impl)
            implementation(projects.library.permissions.impl)
            implementation(projects.database.client)
            implementation(projects.feature.authorization.domain.impl)
            implementation(projects.feature.authorization.data)
            implementation(projects.feature.settings.domain.impl)
            implementation(projects.feature.settings.data)
            implementation(projects.feature.authorization.presentation)
            implementation(projects.feature.authorization.domain.api)
            implementation(projects.feature.appointments.domain.impl)
            implementation(projects.feature.appointments.data)
            implementation(projects.feature.business.domain.impl)
            implementation(projects.feature.business.data)
            implementation(projects.feature.dashboard.presentation)
            implementation(projects.feature.booking.domain.impl)
            implementation(projects.feature.booking.data)
            implementation(projects.feature.booking.presentation)
            implementation(projects.feature.settings.presentation)
            implementation(projects.feature.booking.domain.api)
            implementation(projects.feature.services.domain.impl)
            implementation(projects.feature.services.data)
            implementation(projects.feature.employees.domain.impl)
            implementation(projects.feature.employees.data)
            implementation(libs.koin.core)
            implementation(libs.okio)
            implementation(libs.kmm.resources)
            implementation(libs.kobweb.compose.runtime)
            implementation(libs.compose.html.core)
            implementation(libs.kobweb.core)
            implementation(libs.kobweb.silk)
        }
        jsTest.dependencies {
            implementation(projects.core.testFixtures)
            implementation(projects.feature.appointments.domain.api)
            implementation(projects.feature.business.domain.api)
            implementation(projects.feature.services.domain.api)
            implementation(projects.feature.booking.domain.api)
            implementation(projects.feature.booking.data.source)
            implementation(projects.feature.employees.domain.api)
            implementation(projects.core.data)
            implementation(projects.core.presentation)
            implementation(projects.feature.authorization.domain.api)
            implementation(projects.database.schema)
            implementation(libs.ktor.client.core)
            implementation(libs.koin.core)
            implementation(libs.test.kotlin)
            implementation(libs.kotlinx.coroutines.test)
        }
    }
}

multiplatformResources {
    resourcesPackage.set("me.bookk.client.resources")
    resourcesClassName.set("ClientRes")
}

configurations.configureEach {
    exclude(group = "org.jetbrains.kotlin", module = "kotlin-dom-api-compat")
}

tasks.withType<org.jetbrains.kotlin.gradle.targets.js.ir.KotlinJsIrLink>().configureEach {
    compilerOptions.freeCompilerArgs.add("-Xpartial-linkage-loglevel=ERROR")
}
