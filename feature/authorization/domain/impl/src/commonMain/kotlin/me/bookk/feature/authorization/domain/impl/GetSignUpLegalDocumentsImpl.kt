package me.bookk.feature.authorization.domain.impl

import me.bookk.feature.authorization.domain.api.GetSignUpLegalDocuments
import me.bookk.feature.authorization.domain.api.GetSignUpLegalDocuments.Error
import me.bookk.feature.authorization.domain.datasource.SignUpLegalDocumentsDataSource
import me.bookk.feature.authorization.domain.entity.SignUpLegalDocument
import me.bookk.feature.authorization.domain.entity.SignUpLegalDocument.Document
import me.bookk.feature.authorization.domain.entity.SignUpLegalDocuments
import me.bookk.feature.settings.domain.api.GetLegalDocumentVersions
import me.bookk.feature.settings.domain.api.entity.LegalDocument

internal class GetSignUpLegalDocumentsImpl(
    private val getLegalDocumentVersions: GetLegalDocumentVersions,
    private val dataSource: SignUpLegalDocumentsDataSource,
) : GetSignUpLegalDocuments {
    override suspend fun invoke(): SignUpLegalDocuments {
        return dataSource.getDocuments() ?: throw Error.NotCached()
    }

    override suspend fun refresh(): SignUpLegalDocuments {
        val documents = getLegalDocumentVersions().map {
            SignUpLegalDocument(
                document = when (it.document) {
                    LegalDocument.PRIVACY_POLICY -> Document.PRIVACY_POLICY
                    LegalDocument.TERMS_OF_SERVICE -> Document.TERMS_OF_SERVICE
                },
                version = it.version,
                url = it.url,
            )
        }
        return SignUpLegalDocuments(
            privacyPolicy = documents.firstOrNull { it.document == Document.PRIVACY_POLICY }
                ?: throw Error.MissingDocument(Document.PRIVACY_POLICY),
            termsOfService = documents.firstOrNull { it.document == Document.TERMS_OF_SERVICE }
                ?: throw Error.MissingDocument(Document.TERMS_OF_SERVICE),
        ).also { dataSource.saveDocuments(it) }
    }
}
