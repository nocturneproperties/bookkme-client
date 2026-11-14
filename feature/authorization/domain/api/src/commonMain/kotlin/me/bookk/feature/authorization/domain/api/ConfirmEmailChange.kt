package me.bookk.feature.authorization.domain.api

interface ConfirmEmailChange {
    suspend operator fun invoke(change: StartEmailChange.PendingEmailChange, code: String)

    sealed class Error : Throwable() {
        class EmailAlreadyExist : Error()
    }
}
