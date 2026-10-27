package me.bookk.feature.settings.domain.datasource.passkey

data class AddPasskeyChallenge(
    val requestId: String,
    val displayName: String,
    val jsonChallengeData: String,
    val challenge: String,
    val userHandle: String
)