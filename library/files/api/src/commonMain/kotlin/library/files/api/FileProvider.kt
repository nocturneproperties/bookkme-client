package library.files.api

import okio.Path

interface FileProvider {
    fun touchPrefsFile(fileName: String): Path
    fun touchCacheFile(fileName: String): Path
}