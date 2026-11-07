package me.bookk.feature.business.data.datasource

import library.files.api.FileProvider
import okio.Path
import okio.Path.Companion.toPath

internal class TestExportFileProvider : FileProvider {
    val files = mutableMapOf<Path, String>()

    override fun touchPrefsFile(fileName: String): Path {
        error("Export files must not use preferences")
    }

    override fun touchCacheFile(fileName: String): Path {
        return "/cache".toPath() / fileName
    }

    override fun exists(file: Path): Boolean {
        return file in files
    }

    override fun writeText(file: Path, content: String) {
        files[file] = content
    }

    override fun deleteRecursively(path: Path) {
        files.keys.removeAll { it == path || it.toString().startsWith("$path/") }
    }

    fun clear() {
        files.clear()
    }
}
