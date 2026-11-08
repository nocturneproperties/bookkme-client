package me.bookk.feature.authorization.domain.api

import me.bookk.feature.authorization.domain.entity.EmailVerification

interface StartEmailChange {
    class PendingEmailChange(
        val email: String,
        val verification: EmailVerification,
    )

    suspend operator fun invoke(email: String): PendingEmailChange

    sealed class Error : Throwable() {
        class EmailAlreadyExist : Error()
        class InvalidEmailFormat : Error()
    }
}
