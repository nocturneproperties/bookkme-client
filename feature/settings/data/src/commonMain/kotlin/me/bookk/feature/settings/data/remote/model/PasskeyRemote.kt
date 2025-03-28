package me.bookk.feature.settings.data.remote.model

import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable

@Serializable
class PasskeyRemote(
    val id: Long,
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