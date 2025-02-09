package me.bookk.feature.authorization.domain.api

import me.bookk.feature.authorization.domain.entity.UserProfile

interface UserProfileCRUD {
    suspend fun get(): UserProfile
    suspend fun update(profile: UserProfile)
    suspend fun delete(id: Long)
}