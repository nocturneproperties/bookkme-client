package me.bookk.feature.authorization.data.remote.model

import kotlinx.serialization.Serializable

@Serializable
class AuthChallengeResponse(
    val requestId: String,
    val challengeJson: String
)

@Serializable
class VerifyAuthRequest(
    val requestId: String,
    val publicKeyCredentialJson: String,
    val deviceInfo: DeviceInfo
) {
    @Serializable
    data class DeviceInfo(
        val deviceUUID: String,
        val deviceName: String,
    )
}