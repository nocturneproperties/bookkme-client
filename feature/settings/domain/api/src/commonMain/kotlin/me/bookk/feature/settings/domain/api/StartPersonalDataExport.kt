package me.bookk.feature.settings.domain.api

import me.bookk.core.domain.entity.DataExport
import me.bookk.feature.settings.domain.api.entity.ActionOutcome

interface StartPersonalDataExport {
    suspend operator fun invoke(): ActionOutcome<DataExport>

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
