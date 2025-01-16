package me.bookk.feature.authorization.domain.datasource.registration

sealed class PasskeyVerificationError : Exception() {
    data object UserCancelled : Error()
    data object Unknown : Error()
    data object Infrastructure : Error()
}

class PasskeyVerificationPayload(
    val jsonPayload: String
)