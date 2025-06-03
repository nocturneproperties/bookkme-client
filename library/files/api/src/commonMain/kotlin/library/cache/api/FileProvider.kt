package library.cache.api

import okio.Path

interface FileProvider {
    fun touchPrefsFile(fileName: String): Path
}