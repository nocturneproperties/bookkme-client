package me.bookk.feature.settings.domain.api

import me.bookk.core.domain.entity.DataExport
import kotlin.uuid.Uuid

interface DownloadPersonalDataExport {
    suspend operator fun invoke(): String

    sealed interface Error {
        class NotReady :
            Exception(),
            Error
    }
}
