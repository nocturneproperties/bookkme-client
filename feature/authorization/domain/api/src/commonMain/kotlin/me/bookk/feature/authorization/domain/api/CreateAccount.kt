package me.bookk.feature.authorization.domain.api

import me.bookk.feature.authorization.domain.entity.AcceptedLegalDocuments

interface CreateAccount {
    class UserData(
        val firstName: String,
        val lastName: String,
        val email: String,
        val acceptedDocuments: AcceptedLegalDocuments = AcceptedLegalDocuments(),
    )

    suspend operator fun invoke(userData: UserData)

    sealed class Error : Throwable() {
        class EmailAlreadyExist : Error()
        class InvalidEmailFormat : Error()
        class PasskeyVerificationFailed : Error()
        class AccountCreationFailed : Error()
        class ChallengeWindowExpired : Error()
        class LegalDocumentVersionUnknown : Error()
        class LegalDocumentsNotAccepted : Error()
    }
}
