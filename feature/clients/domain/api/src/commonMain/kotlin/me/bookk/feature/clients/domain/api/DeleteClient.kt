package me.bookk.feature.clients.domain.api

import me.bookk.feature.clients.domain.api.entity.Client

interface DeleteClient {
    suspend operator fun invoke(client: Client)

    sealed interface Error {
        class ConfirmationFailed :
            Exception(),
            Error
        class ConfirmationRequired :
            Exception(),
            Error
    }
}
