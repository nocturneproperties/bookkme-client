package me.bookk.feature.authorization.domain.entity

sealed class EmailVerificationError : Throwable() {
    class CodeInvalid : EmailVerificationError()
    class CodeExpired : EmailVerificationError()
    class AttemptsExceeded : EmailVerificationError()
    class ResendTooSoon : EmailVerificationError()
    class Undeliverable : EmailVerificationError()
}
