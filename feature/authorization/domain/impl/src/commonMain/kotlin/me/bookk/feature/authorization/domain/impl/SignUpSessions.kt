package me.bookk.feature.authorization.domain.impl

import me.bookk.feature.authorization.domain.api.FinishSignUp
import me.bookk.feature.authorization.domain.api.StartSignUp
import me.bookk.feature.authorization.domain.datasource.registration.SignUpSession
import me.bookk.feature.authorization.domain.entity.EmailVerificationError
import me.bookk.feature.authorization.domain.entity.SignUpRejection

internal fun SignUpSession.dropped(rejection: SignUpRejection?): SignUpSession {
    return copy(challenge = null, attestation = null, rejection = rejection)
}

internal fun Throwable.toSignUpRejection(): SignUpRejection? {
    return when (this) {
        is StartSignUp.Error.EmailAlreadyExist,
        is FinishSignUp.Error.EmailAlreadyExist,
        -> SignUpRejection.EMAIL_TAKEN

        is StartSignUp.Error.InvalidEmailFormat,
        is FinishSignUp.Error.InvalidEmailFormat,
        -> SignUpRejection.EMAIL_INVALID

        is EmailVerificationError.Undeliverable -> SignUpRejection.EMAIL_UNDELIVERABLE

        is FinishSignUp.Error.LegalDocumentVersionUnknown,
        is FinishSignUp.Error.LegalDocumentsNotAccepted,
        -> SignUpRejection.LEGAL_DOCUMENTS_CHANGED

        else -> null
    }
}
