package me.bookk.feature.authorization.data.remote.model

import kotlinx.serialization.Serializable

@Serializable
class UserProfileRemote(
    val id: Long,
    val firstName: String,
    val lastName: String,
    val email: String
)