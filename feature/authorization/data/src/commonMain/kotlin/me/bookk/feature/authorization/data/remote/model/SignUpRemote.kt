package me.bookk.feature.authorization.data.remote.model

import kotlinx.serialization.Serializable
import kotlinx.serialization.protobuf.ProtoNumber

@Serializable
data class PassKeySignUpStartInfo(
    @ProtoNumber(1) val firstName: String,
    @ProtoNumber(2) val lastName: String,
    @ProtoNumber(3) val email: String
)

@Serializable
class RegistrationChallengeResponse(
    @ProtoNumber(1) val requestId: String,
    @ProtoNumber(2) val challenge: String,
    @ProtoNumber(3) val challengeJson: String,
    @ProtoNumber(4) val userHandle: String,
    @ProtoNumber(5) val displayName: String,
)

@Serializable
class VerifyAccountCreationRequest(
    @ProtoNumber(1) val requestId: String,
    @ProtoNumber(2) val deviceInfo: DeviceInfo,
    @ProtoNumber(3) val userInfo: UserInfo,
    @ProtoNumber(4) val publicKeyCredentialJson: String
) {
    @Serializable
    class UserInfo(
        @ProtoNumber(1) val name: String,
        @ProtoNumber(2) val lastName: String,
        @ProtoNumber(3) val email: String
    )

    @Serializable
    class DeviceInfo(
        @ProtoNumber(1) val deviceUUID: String,
        @ProtoNumber(2) val deviceName: String,
    )
}
