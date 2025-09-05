package me.bookk.feature.authorization.domain.api

import me.bookk.feature.authorization.domain.entity.UserProfile
import kotlin.uuid.Uuid

interface UserProfileCRUD {
    suspend fun updateFromRemote()
    suspend fun get(): UserProfile
    suspend fun update(profile: UserProfile)
    suspend fun delete(id: Uuid)
}