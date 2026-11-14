package me.bookk.feature.authorization.domain.entity

enum class SignUpRejection {
    EMAIL_TAKEN,
    EMAIL_INVALID,
    EMAIL_UNDELIVERABLE,
    LEGAL_DOCUMENTS_CHANGED,
}
