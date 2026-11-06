package me.bookk.feature.authorization.data.local

import kotlinx.serialization.Serializable
import me.bookk.feature.authorization.domain.entity.SignUpLegalDocument
import me.bookk.feature.authorization.domain.entity.SignUpLegalDocument.Document
import me.bookk.feature.authorization.domain.entity.SignUpLegalDocuments

@Serializable
internal data class SignUpLegalDocumentsLocal(
    val privacyPolicyVersion: String,
    val privacyPolicyUrl: String,
    val termsOfServiceVersion: String,
    val termsOfServiceUrl: String,
) {
    fun toDomain(): SignUpLegalDocuments {
        return SignUpLegalDocuments(
            privacyPolicy = SignUpLegalDocument(
                Document.PRIVACY_POLICY,
                privacyPolicyVersion,
                privacyPolicyUrl,
            ),
            termsOfService = SignUpLegalDocument(
                Document.TERMS_OF_SERVICE,
                termsOfServiceVersion,
                termsOfServiceUrl,
            ),
        )
    }
}
