package me.bookk.feature.authorization.domain.api

interface FinishSignUp {
    suspend operator fun invoke(code: String)

    sealed class Error : Throwable() {
        class SignUpNotStarted : Error()
        class EmailAlreadyExist : Error()
        class InvalidEmailFormat : Error()
        class PasskeyVerificationFailed : Error()
        class AccountCreationFailed : Error()
        class ChallengeWindowExpired : Error()
        class LegalDocumentVersionUnknown : Error()
        class LegalDocumentsNotAccepted : Error()
    }
}
