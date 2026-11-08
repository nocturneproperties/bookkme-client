package me.bookk.feature.authorization.domain.api

import me.bookk.feature.authorization.domain.entity.AcceptedLegalDocuments
import kotlin.time.Instant

interface StartSignUp {
    class UserData(
        val firstName: String,
        val lastName: String,
        val email: String,
        val acceptedDocuments: AcceptedLegalDocuments = AcceptedLegalDocuments(),
    )

    class PendingSignUp(
        val maskedEmail: String,
        val expiresAt: Instant,
        val resendAvailableAt: Instant,
    )

    suspend operator fun invoke(userData: UserData): PendingSignUp

    sealed class Error : Throwable() {
        class EmailAlreadyExist : Error()
        class InvalidEmailFormat : Error()
    }
}
