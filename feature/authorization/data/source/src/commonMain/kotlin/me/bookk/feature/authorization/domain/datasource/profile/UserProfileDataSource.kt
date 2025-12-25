package me.bookk.feature.authorization.domain.datasource.profile

import me.bookk.feature.authorization.domain.entity.UserProfile
import kotlin.uuid.Uuid

interface UserProfileDataSource {
    suspend fun getProfileFromDatabase(): UserProfile?
    suspend fun getProfileFromBackend(): UserProfile
    suspend fun upsertProfile(profile: UserProfile)
    suspend fun updateProfile(userProfile: UserProfile)
    suspend fun deleteProfile(id: Uuid)
}