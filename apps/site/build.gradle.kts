import build_src.site.DesignSystemColors
import build_src.site.GenerateSiteBuildConfigTask
import build_src.site.GenerateSitemapTask
import build_src.site.GenerateWebManifestTask
import build_src.site.LegalVersionPaths
import build_src.site.MokoStrings
import build_src.site.SitePages
import build_src.site.WebManifest
import com.varabyte.kobweb.gradle.application.util.configAsKobwebApplication
import kotlinx.html.link
import kotlinx.html.meta

plugins {
    id(libs.plugins.convention.kmm.web.get().pluginId)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.kobweb.application)
    alias(libs.plugins.kmm.resources)
    alias(libs.plugins.mockery)
    id(libs.plugins.convention.site.legal.get().pluginId)
    id(libs.plugins.convention.site.securitytxt.get().pluginId)
    id(libs.plugins.convention.site.webmanifest.get().pluginId)
    id(libs.plugins.convention.site.sitemap.get().pluginId)
    id(libs.plugins.convention.site.buildconfig.get().pluginId)
}

group = "me.bookk.site"
version = "0.1.1"

val siteStrings = MokoStrings.read(file("src/commonMain/moko-resources/base/strings.xml"))
val siteName = siteStrings.value("site_app_name")
val siteDescription = siteStrings.value("site_description")
val siteOrigin = "https://bookkme.com"
val designSystemColors = DesignSystemColors.read(rootDir)
val sitePagePaths = listOf("/", "/clients", "/guides", "/contact", "/privacy", "/terms", "/dpa")
val siteDefaultLanguage = "en"
val siteOtherLanguages = listOf("uk", "pl")
val siteVersionedPaths = LegalVersionPaths.read(file("legal"))
val sitePages = SitePages(
    pagePaths = sitePagePaths,
    versionedPaths = siteVersionedPaths,
    defaultLanguage = siteDefaultLanguage,
    otherLanguages = siteOtherLanguages,
)

val generateWebManifest = tasks.named<GenerateWebManifestTask>("generateWebManifest") {
    manifestName.set(siteName)
    manifestDescription.set(siteDescription)
    themeColor.set(designSystemColors.opaque("lightBackground"))
    backgroundColor.set(designSystemColors.opaque("lightBackground"))
}

val generateSiteBuildConfig = tasks.named<GenerateSiteBuildConfigTask>("generateSiteBuildConfig") {
    origin.set(siteOrigin)
    pagePaths.set(sitePagePaths)
    languages.set(listOf(siteDefaultLanguage) + siteOtherLanguages)
}

val generateSitemap = tasks.named<GenerateSitemapTask>("generateSitemap") {
    origin.set(siteOrigin)
    pagePaths.set(sitePagePaths)
    versionedPaths.set(siteVersionedPaths)
    defaultLanguage.set(siteDefaultLanguage)
    otherLanguages.set(siteOtherLanguages)
}

kobweb {
    app {
        index {
            val lightBackground = designSystemColors.opaque("lightBackground")
            val darkBackground = designSystemColors.opaque("darkBackground")
            val shareImage = "$siteOrigin/share-image.png"
            val name = siteName
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
                meta(content = "website") { attributes["property"] = "og:type" }
                meta(content = name) { attributes["property"] = "og:site_name" }
                meta(content = shareImage) { attributes["property"] = "og:image" }
                meta(content = "1200") { attributes["property"] = "og:image:width" }
                meta(content = "630") { attributes["property"] = "og:image:height" }
                meta(name = "twitter:card", content = "summary_large_image")
            }
        }
        export {
            suppressLayoutWarning.set(true)
            sitePages.extraExportRoutes().forEach { addExtraRoute(it) }
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
            resources.srcDir(generateSitemap)
            kotlin.srcDir(tasks.named("generateLegalTexts"))
            kotlin.srcDir(generateSiteBuildConfig)
        }
        jsMain.dependencies {
            implementation(npm("@sqlite.org/sqlite-wasm", libs.versions.npm.sqlite.wasm.get()))
            implementation(projects.site.designsystem)
            implementation(projects.site.domcompat)
            implementation(projects.library.telegram)
            implementation(projects.shared.common)
            implementation(projects.library.device.impl)
            implementation(projects.library.cache.impl)
            implementation(projects.library.files.impl)
            implementation(projects.database.client)
            implementation(projects.feature.authorization.domain.impl)
            implementation(projects.feature.authorization.data)
            implementation(projects.feature.settings.domain.impl)
            implementation(projects.feature.settings.data)
            implementation(projects.feature.authorization.presentation)
            implementation(projects.feature.authorization.domain.api)
            implementation(libs.koin.core)
            implementation(libs.kmm.resources)
            implementation(libs.kobweb.compose.runtime)
            implementation(libs.compose.html.core)
            implementation(libs.kobweb.core)
            implementation(libs.kobweb.silk)
            implementation(libs.kotlinx.datetime)
        }
        jsTest.dependencies {
            implementation(projects.core.testFixtures)
            implementation(projects.core.data)
            implementation(projects.core.presentation)
            implementation(projects.feature.authorization.domain.api)
            implementation(projects.database.account)
            implementation(libs.ktor.client.core)
            implementation(libs.koin.core)
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
