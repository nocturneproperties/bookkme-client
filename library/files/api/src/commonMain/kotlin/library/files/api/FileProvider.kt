package library.files.api

import okio.Path

interface FileProvider {
    fun touchPrefsFile(fileName: String): Path
    fun touchCacheFile(fileName: String): Path
    fun exists(file: Path): Boolean
    fun writeText(file: Path, content: String)
    fun deleteRecursively(path: Path)
}
