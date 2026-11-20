package me.bookk.feature.authorization.domain.api

import me.bookk.feature.authorization.domain.entity.AcceptedLegalDocuments

interface SignInWithTelegram {
    suspend operator fun invoke(initData: String, acceptedDocuments: AcceptedLegalDocuments?)

    sealed class Error : Throwable() {
        class TelegramDataInvalid : Error()
        class TelegramDataExpired : Error()
        class Unavailable : Error()
        class AccountCreationFailed : Error()
        class LegalDocumentVersionUnknown : Error()
        class LegalDocumentsNotAccepted : Error()
    }
}
