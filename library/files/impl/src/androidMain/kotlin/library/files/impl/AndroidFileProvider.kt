package library.files.impl

import android.content.Context
import okio.FileSystem
import okio.Path
import okio.Path.Companion.toPath
import okio.SYSTEM

class AndroidFileProvider(
    private val context: Context,
) : CommonFileProvider(FileSystem.SYSTEM) {
    override fun touchPrefsFile(fileName: String): Path {
        return context.filesDir.resolve(fileName).absolutePath.toPath()
    }

    override fun touchCacheFile(fileName: String): Path {
        return context.cacheDir.resolve(fileName).absolutePath.toPath()
    }
}
