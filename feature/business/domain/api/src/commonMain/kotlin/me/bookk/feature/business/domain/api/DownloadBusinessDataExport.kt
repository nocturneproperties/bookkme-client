package me.bookk.feature.business.domain.api

import okio.Path
import kotlin.uuid.Uuid

interface DownloadBusinessDataExport {
    suspend operator fun invoke(businessId: Uuid): Path

    sealed interface Error {
        class NotReady :
            Exception(),
            Error
    }
}
