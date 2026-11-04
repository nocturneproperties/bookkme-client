package me.bookk.feature.settings.domain.api

import me.bookk.core.domain.entity.DataExport
import kotlin.uuid.Uuid

interface StartPersonalDataExport {
    suspend operator fun invoke(): DataExport

    sealed interface Error {
        class ConfirmationRequired : Exception(), Error
        class ConfirmationFailed : Exception(), Error
        class TooFrequent : Exception(), Error
    }
}
