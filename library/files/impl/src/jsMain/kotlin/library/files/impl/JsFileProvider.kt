package library.files.impl

import library.files.api.FileProvider
import okio.Path
import okio.Path.Companion.toPath

class JsFileProvider : FileProvider {
    private val files = mutableMapOf<Path, String>()

    override fun touchPrefsFile(fileName: String): Path {
        return PREFS_DIRECTORY / fileName
    }

    override fun touchCacheFile(fileName: String): Path {
        return CACHE_DIRECTORY / fileName
    }

    override fun exists(file: Path): Boolean {
        return file in files
    }

    override fun writeText(file: Path, content: String) {
        files[file] = content
    }

    override fun deleteRecursively(path: Path) {
        files.keys.removeAll { it == path || it.isInside(path) }
    }

    private fun Path.isInside(directory: Path): Boolean {
        return generateSequence(parent) { it.parent }.any { it == directory }
    }

    private companion object {
        val PREFS_DIRECTORY = "/prefs".toPath()
        val CACHE_DIRECTORY = "/cache".toPath()
    }
}
