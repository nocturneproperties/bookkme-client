package me.bookk.shared.data

import android.content.Context
import me.bookk.core.storage.FileProvider
import okio.Path
import okio.Path.Companion.toPath

actual class FileProviderImpl(private val context: Context) : FileProvider {
    override fun touchPrefsFile(fileName: String): Path {
        return context.filesDir.resolve(fileName).absolutePath.toPath()
    }
}