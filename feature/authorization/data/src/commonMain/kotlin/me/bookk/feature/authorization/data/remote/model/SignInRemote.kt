package me.bookk.feature.authorization.data.remote.model

import kotlinx.serialization.Serializable
import kotlinx.serialization.protobuf.ProtoNumber

@Serializable
class AuthChallengeResponse(
    @ProtoNumber(1) val requestId: String,
    @ProtoNumber(2) val challengeJson: String,
    @ProtoNumber(3) val challenge: String
)

@Serializable
class VerifyAuthRequest(
    @ProtoNumber(1) val requestId: String,
    @ProtoNumber(2) val publicKeyCredentialJson: String,
    @ProtoNumber(3) val deviceInfo: DeviceInfo
) {
    @Serializable
    data class DeviceInfo(
        @ProtoNumber(1) val deviceUUID: String,
        @ProtoNumber(2) val deviceName: String,
    )
}
