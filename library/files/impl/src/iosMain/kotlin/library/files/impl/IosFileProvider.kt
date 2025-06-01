package library.files.impl

import kotlinx.cinterop.ExperimentalForeignApi
import library.cache.api.FileProvider
import okio.Path
import okio.Path.Companion.toPath
import platform.Foundation.NSDocumentDirectory
import platform.Foundation.NSFileManager
import platform.Foundation.NSURL
import platform.Foundation.NSUserDomainMask

class IosFileProvider : FileProvider {

    @OptIn(ExperimentalForeignApi::class)
    override fun touchPrefsFile(fileName: String): Path {
        val documentDirectory: NSURL? = NSFileManager.defaultManager.URLForDirectory(
            directory = NSDocumentDirectory,
            inDomain = NSUserDomainMask,
            appropriateForURL = null,
            create = false,
            error = null,
        )
        val fullPath = requireNotNull(documentDirectory).path + "/$fileName"
        return fullPath.toPath()
    }
}