package me.bookk.feature.platform.data

import android.content.Context
import me.bookk.feature.platform.domain.datasource.FileProvider
import okio.Path
import okio.Path.Companion.toPath

class AndroidFileProvider(private val context: Context) : FileProvider {
    override fun touchPrefsFile(fileName: String): Path {
        return context.filesDir.resolve(fileName).absolutePath.toPath()
    }
}