package me.bookk.feature.authorization.data.remote.model

import kotlinx.serialization.Serializable

@Serializable
class SignInStartResponse(
    val requestId: String,
    val challengeJson: String
)

@Serializable
class VerifySignInRequest(
    val requestId: String,
    val deviceInfo: DeviceInfo,
    val publicKeyCredentialJson: String
) {
    @Serializable
    data class DeviceInfo(
        val deviceUUID: String,
        val deviceName: String,
    )
}