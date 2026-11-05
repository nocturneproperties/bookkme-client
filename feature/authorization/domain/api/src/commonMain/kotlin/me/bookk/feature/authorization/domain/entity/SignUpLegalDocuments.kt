package me.bookk.feature.authorization.domain.entity

data class SignUpLegalDocuments(
    val privacyPolicy: SignUpLegalDocument,
    val termsOfService: SignUpLegalDocument,
)
