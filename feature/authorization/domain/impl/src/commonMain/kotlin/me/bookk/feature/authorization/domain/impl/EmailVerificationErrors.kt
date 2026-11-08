package me.bookk.feature.authorization.domain.impl

import me.bookk.core.domain.entity.Error
import me.bookk.feature.authorization.domain.datasource.AuthErrorCodes
import me.bookk.feature.authorization.domain.entity.EmailVerificationError

internal fun Error.BusinessError.toEmailVerificationError(): EmailVerificationError? {
    return when (errorCode) {
        AuthErrorCodes.EMAIL_CODE_INVALID -> EmailVerificationError.CodeInvalid()
        AuthErrorCodes.EMAIL_CODE_EXPIRED -> EmailVerificationError.CodeExpired()
        AuthErrorCodes.EMAIL_CODE_ATTEMPTS_EXCEEDED -> EmailVerificationError.AttemptsExceeded()
        AuthErrorCodes.EMAIL_CODE_RESEND_TOO_SOON -> EmailVerificationError.ResendTooSoon()
        AuthErrorCodes.EMAIL_UNDELIVERABLE -> EmailVerificationError.Undeliverable()
        else -> null
    }
}
