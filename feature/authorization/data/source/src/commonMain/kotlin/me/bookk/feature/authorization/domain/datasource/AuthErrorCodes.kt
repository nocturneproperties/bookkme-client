package me.bookk.feature.authorization.domain.datasource

object AuthErrorCodes {
    const val EMAIL_EXIST = 1
    const val INVALID_EMAIL_FORMAT = 2
    const val USER_ALREADY_EXIST = 3
    const val VERIFICATION_FAILED = 4
    const val ACCOUNT_CREATION_FAILED = 5
    const val CHALLENGE_WINDOW_EXPIRED = 6
    const val PASSKEY_OWNER_NOT_FOUND = 7
    const val PASSKEY_NOT_OWNED = 11
    const val ACTION_CONFIRMATION_INVALID = 12
    const val LEGAL_DOCUMENT_VERSION_UNKNOWN = 100003
    const val LEGAL_DOCUMENTS_NOT_ACCEPTED = 100004
    const val EMAIL_CODE_INVALID = 910001
    const val EMAIL_CODE_EXPIRED = 910002
    const val EMAIL_CODE_ATTEMPTS_EXCEEDED = 910003
    const val EMAIL_CODE_RESEND_TOO_SOON = 910004
    const val EMAIL_UNDELIVERABLE = 910005
}
