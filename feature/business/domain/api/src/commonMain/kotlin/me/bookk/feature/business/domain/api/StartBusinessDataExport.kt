package me.bookk.feature.business.domain.api

import me.bookk.core.domain.entity.DataExport
import kotlin.uuid.Uuid

interface StartBusinessDataExport {
    suspend operator fun invoke(businessId: Uuid): DataExport

    sealed interface Error {
        class ConfirmationRequired :
            Exception(),
            Error
        class ConfirmationFailed :
            Exception(),
            Error
        class TooFrequent :
            Exception(),
            Error
    }
}
