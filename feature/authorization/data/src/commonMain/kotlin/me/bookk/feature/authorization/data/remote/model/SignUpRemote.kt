package me.bookk.feature.authorization.data.remote.model

import kotlinx.serialization.Serializable

@Serializable
data class PassKeySignUpStartInfo(
    val firstName: String,
    val lastName: String,
    val email: String
)

@Serializable
class RegistrationChallengeResponse(
    val challenge: String,
    val displayName: String,
    val userId: String
)

@Serializable
class VerifyAccountCreationRequest(
    val deviceInfo: DeviceInfo,
    val userInfo: UserInfo,
    val publicKeyCredentialJson: String
) {
    @Serializable
    class UserInfo(
        val userId: String,
        val name: String,
        val lastName: String,
        val email: String
    )

    @Serializable
    class DeviceInfo(
        val deviceUUID: String,
        val deviceName: String,
    )
}