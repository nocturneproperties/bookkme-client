package me.bookk.feature.business.domain.api

import me.bookk.core.domain.entity.DataExport
import kotlin.uuid.Uuid

interface DownloadBusinessDataExport {
    suspend operator fun invoke(businessId: Uuid): String

    sealed interface Error {
        class NotReady : Exception(), Error
    }
}
