package me.bookk.feature.authorization.domain.datasource.registration

class ServerSignUpChallenge(
    val userId: String,
    val displayName: String,
    val jsonChallengeData: String
)