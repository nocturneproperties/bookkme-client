package me.bookk.feature.authorization.data.remote.model

import kotlinx.serialization.Serializable
import kotlinx.serialization.protobuf.ProtoNumber
import kotlin.time.Instant
import kotlin.uuid.Uuid

@Serializable
data class PassKeySignUpStartInfo(
    @ProtoNumber(1) val firstName: String,
    @ProtoNumber(2) val lastName: String,
    @ProtoNumber(3) val email: String,
)

@Serializable
class EmailVerificationStartedRemote(
    @ProtoNumber(1) val verificationId: Uuid,
    @ProtoNumber(2) val expiresAt: Instant,
    @ProtoNumber(3) val resendAvailableAt: Instant,
)

@Serializable
class SignUpChallengeResponseRemote(
    @ProtoNumber(1) val requestId: String,
    @ProtoNumber(2) val challenge: String,
    @ProtoNumber(3) val challengeJson: String,
    @ProtoNumber(4) val userHandle: String,
    @ProtoNumber(5) val displayName: String,
    @ProtoNumber(6) val emailVerification: EmailVerificationStartedRemote,
)

@Serializable
class VerifyAccountCreationRequest(
    @ProtoNumber(1) val requestId: String,
    @ProtoNumber(2) val deviceInfo: DeviceInfo,
    @ProtoNumber(3) val userInfo: UserInfo,
    @ProtoNumber(4) val publicKeyCredentialJson: String,
    @ProtoNumber(5) val acceptedDocuments: AcceptedLegalDocumentsRemote,
    @ProtoNumber(6) val verificationId: Uuid,
    @ProtoNumber(7) val emailCode: String,
) {
    @Serializable
    class UserInfo(
        @ProtoNumber(1) val name: String,
        @ProtoNumber(2) val lastName: String,
        @ProtoNumber(3) val email: String,
    )

    @Serializable
    class DeviceInfo(
        @ProtoNumber(1) val deviceUUID: String,
        @ProtoNumber(2) val deviceName: String,
    )
}

@Serializable
class AcceptedLegalDocumentsRemote(
    @ProtoNumber(1) val privacyPolicy: String? = null,
    @ProtoNumber(2) val termsOfService: String? = null,
)
