package me.bookk.feature.authorization.domain.datasource.authorization

class ServerAuthenticationChallenge(
    val requestId: String,
    val challengeJson: String,
    val challenge: String
)