package me.bookk.feature.authorization.domain.api

import kotlinx.coroutines.flow.Flow
import me.bookk.feature.authorization.domain.entity.UserProfile
import kotlin.uuid.Uuid

interface UserProfileCRUD {
    suspend fun updateFromRemote()
    suspend fun get(): UserProfile
    fun observe(): Flow<UserProfile?>
    suspend fun update(profile: UserProfile)
    suspend fun delete(id: Uuid)
}