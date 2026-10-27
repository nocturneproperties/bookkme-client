package me.bookk.feature.settings.data.remote.model

import kotlinx.serialization.Serializable
import kotlinx.serialization.protobuf.ProtoNumber
import kotlin.time.Instant
import kotlin.uuid.Uuid

@Serializable
class PasskeyRemote(
    @ProtoNumber(1) val id: Uuid,
    @ProtoNumber(2) val name: String,
    @ProtoNumber(3) val createdAt: Instant,
    @ProtoNumber(4) val lastUsedAt: Instant,
    @ProtoNumber(5) val isBackedUp: Boolean
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
class AddPasskeyRequest(
    @ProtoNumber(1) val requestId: String,
    @ProtoNumber(2) val publicKeyCredentialJson: String
)
