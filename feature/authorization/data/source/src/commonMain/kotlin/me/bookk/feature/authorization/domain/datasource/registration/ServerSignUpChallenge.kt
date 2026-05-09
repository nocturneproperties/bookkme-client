package me.bookk.feature.authorization.domain.datasource.registration

class ServerSignUpChallenge(
    val requestId: String,
    val displayName: String,
    val jsonChallengeData: String,
    val challenge: String,
    val userHandle: String
)