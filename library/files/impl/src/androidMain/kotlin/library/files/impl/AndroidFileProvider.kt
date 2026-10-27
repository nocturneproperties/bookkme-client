package library.files.impl

import android.content.Context
import library.files.api.FileProvider
import okio.Path
import okio.Path.Companion.toPath

class AndroidFileProvider(private val context: Context) : FileProvider {
    override fun touchPrefsFile(fileName: String): Path {
        return context.filesDir.resolve(fileName).absolutePath.toPath()
    }

    override fun touchCacheFile(fileName: String): Path {
        return context.cacheDir.resolve(fileName).absolutePath.toPath()
    }
}