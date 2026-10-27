package me.bookk.feature.authorization.domain.datasource.profile

import kotlinx.coroutines.flow.Flow
import me.bookk.feature.authorization.domain.entity.UserProfile
import kotlin.uuid.Uuid

interface UserProfileDataSource {
    suspend fun getProfileFromDatabase(): UserProfile?
    fun observeProfileFromDatabase(): Flow<UserProfile?>
    suspend fun getProfileFromBackend(): UserProfile
    suspend fun upsertProfile(profile: UserProfile)
    suspend fun updateProfile(userProfile: UserProfile)
    suspend fun deleteProfile(id: Uuid)
}