package me.bookk.feature.settings.data.remote.model

import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable
import kotlin.uuid.Uuid

@Serializable
class PasskeyRemote(
    val id: Uuid,
    val name: String,
    val createdAt: Instant,
    val lastUsedAt: Instant,
    val isBackedUp: Boolean
)

@Serializable
class RegistrationChallengeResponse(
    val requestId: String,
    val challenge: String,
    val displayName: String
)

@Serializable
class AddPasskeyRequest(
    val requestId: String,
    val publicKeyCredentialJson: String
)