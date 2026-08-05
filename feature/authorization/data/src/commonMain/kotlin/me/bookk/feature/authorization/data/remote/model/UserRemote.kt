package me.bookk.feature.authorization.data.remote.model

import kotlinx.serialization.Serializable
import kotlin.uuid.Uuid

@Serializable
internal class UserProfileRemote(
    val id: Uuid,
    val firstName: String,
    val lastName: String,
    val email: String,
    val phone: String?
)