package me.bookk.feature.authorization.domain.entity

import kotlin.uuid.Uuid

data class UserProfile(
    val id: Uuid,
    val firstName: String,
    val lastName: String,
    val email: String
) {
    val fullName: String = "$firstName $lastName"
}