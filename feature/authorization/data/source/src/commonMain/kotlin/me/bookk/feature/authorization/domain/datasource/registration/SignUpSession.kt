package me.bookk.feature.authorization.domain.datasource.registration

import me.bookk.feature.authorization.domain.api.StartSignUp
import me.bookk.feature.authorization.domain.entity.SignUpRejection

data class SignUpSession(
    val userData: StartSignUp.UserData,
    val challenge: ServerSignUpChallenge?,
    val attestation: PasskeyVerificationPayload? = null,
    val rejection: SignUpRejection? = null,
)
