package me.bookk.feature.authorization.domain.datasource.registration

import me.bookk.feature.authorization.domain.entity.EmailVerification

class ServerSignUpChallenge(
    val requestId: String,
    val displayName: String,
    val jsonChallengeData: String,
    val challenge: String,
    val userHandle: String,
    val emailVerification: EmailVerification,
)
