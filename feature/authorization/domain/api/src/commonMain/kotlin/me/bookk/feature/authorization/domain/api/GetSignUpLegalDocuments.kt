package me.bookk.feature.authorization.domain.api

import me.bookk.feature.authorization.domain.entity.SignUpLegalDocument
import me.bookk.feature.authorization.domain.entity.SignUpLegalDocuments

interface GetSignUpLegalDocuments {
    suspend operator fun invoke(): SignUpLegalDocuments
    suspend fun refresh(): SignUpLegalDocuments

    sealed interface Error {
        class NotCached : Exception("Signup legal documents have not been cached")

        class MissingDocument(val document: SignUpLegalDocument.Document) :
            Exception("Required signup legal document is missing: $document")
    }
}
