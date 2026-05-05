package me.bookk.feature.settings.domain.api

interface DeleteAccount {
    suspend operator fun invoke()

    sealed class Error : Throwable() {
        class AccountVerificationFailed : Error()
    }
}