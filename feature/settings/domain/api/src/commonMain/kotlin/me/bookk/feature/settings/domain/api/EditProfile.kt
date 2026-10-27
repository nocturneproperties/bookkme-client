package me.bookk.feature.settings.domain.api

interface EditProfile {
    suspend operator fun invoke(
        firstName: String,
        lastName: String,
        email: String
    )
}