package library.files.impl

import android.content.Context
import library.cache.api.FileProvider
import okio.Path
import okio.Path.Companion.toPath

class AndroidFileProvider(private val context: Context) : FileProvider {
    override fun touchPrefsFile(fileName: String): Path {
        return context.filesDir.resolve(fileName).absolutePath.toPath()
    }
}