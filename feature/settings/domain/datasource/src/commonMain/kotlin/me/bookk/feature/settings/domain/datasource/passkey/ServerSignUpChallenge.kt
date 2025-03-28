package me.bookk.feature.settings.domain.datasource.passkey

class ServerSignUpChallenge(
    val requestId: String,
    val displayName: String,
    val jsonChallengeData: String
)