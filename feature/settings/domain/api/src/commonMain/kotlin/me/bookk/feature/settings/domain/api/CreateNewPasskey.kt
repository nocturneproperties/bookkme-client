package me.bookk.feature.settings.domain.api

import me.bookk.feature.settings.domain.api.entity.Passkey

interface CreateNewPasskey {
    suspend operator fun invoke(): List<Passkey>

    sealed class Error : Throwable() {
        class AccountCreationFailed : Error()
    }
}