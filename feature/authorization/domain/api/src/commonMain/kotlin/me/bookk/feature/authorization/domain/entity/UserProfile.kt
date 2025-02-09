package me.bookk.feature.authorization.domain.entity

data class UserProfile(
    val id: Long,
    val firstName: String,
    val lastName: String,
    val email: String
)