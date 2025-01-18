package me.bookk.feature.platform.domain.datasource

import okio.Path

interface FileProvider {
    fun touchPrefsFile(fileName: String): Path
}