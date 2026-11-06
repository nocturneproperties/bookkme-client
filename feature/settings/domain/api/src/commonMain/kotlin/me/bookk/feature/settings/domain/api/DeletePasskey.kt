package me.bookk.feature.settings.domain.api

import kotlin.uuid.Uuid

interface DeletePasskey {
    suspend operator fun invoke(id: Uuid)

    sealed interface Error {
        class LastPasskey :
            Exception(),
            Error
    }
}
