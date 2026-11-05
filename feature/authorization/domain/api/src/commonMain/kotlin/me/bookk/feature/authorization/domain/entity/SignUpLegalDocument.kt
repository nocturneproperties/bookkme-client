package me.bookk.feature.authorization.domain.entity

data class SignUpLegalDocument(val document: Document, val version: String, val url: String) {
    enum class Document {
        PRIVACY_POLICY,
        TERMS_OF_SERVICE,
    }
}
