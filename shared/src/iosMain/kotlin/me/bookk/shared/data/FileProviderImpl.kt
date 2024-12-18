package me.bookk.shared.data

import kotlinx.cinterop.ExperimentalForeignApi
import me.bookk.core.storage.FileProvider
import okio.Path
import okio.Path.Companion.toPath
import platform.Foundation.NSDocumentDirectory
import platform.Foundation.NSFileManager
import platform.Foundation.NSURL
import platform.Foundation.NSUserDomainMask

actual class FileProviderImpl : FileProvider {

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