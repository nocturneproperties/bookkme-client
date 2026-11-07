package me.bookk.feature.settings.domain.api

import okio.Path

interface DownloadPersonalDataExport {
    suspend operator fun invoke(): Path

    sealed interface Error {
        class NotReady :
            Exception(),
            Error
    }
}
