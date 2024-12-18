package me.bookk.core.storage

import okio.Path

interface FileProvider {
    fun touchPrefsFile(fileName: String): Path
}