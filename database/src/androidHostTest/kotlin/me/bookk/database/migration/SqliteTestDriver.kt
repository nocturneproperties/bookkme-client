package me.bookk.database.migration

import java.io.File
import java.nio.file.Files

internal actual fun prepareSqliteTestDriver() {
    if (System.getProperty("androidx.sqlite.driver.bundled.path") != null) return
    val os = System.getProperty("os.name").orEmpty().lowercase()
    val platform = when {
        os.contains("mac") -> "osx"
        os.contains("win") -> "windows"
        else -> "linux"
    }
    val arch = when (System.getProperty("os.arch").orEmpty()) {
        "aarch64", "arm64" -> "arm64"
        else -> "x64"
    }
    val name = checkNotNull(System.mapLibraryName("sqliteJni"))
    val path = Files.createTempDirectory("bookk-sqlite-test").toFile()
    path.deleteOnExit()
    val library = File(path, name)
    library.deleteOnExit()
    val resource = "natives/${platform}_$arch/$name"
    val classLoader = checkNotNull(Thread.currentThread().contextClassLoader)
    checkNotNull(classLoader.getResourceAsStream(resource)) {
        "Missing SQLite test library: $resource"
    }.use { input -> library.outputStream().use(input::copyTo) }
    System.setProperty("androidx.sqlite.driver.bundled.path", path.absolutePath)
    System.setProperty("androidx.sqlite.driver.bundled.name", name)
}
