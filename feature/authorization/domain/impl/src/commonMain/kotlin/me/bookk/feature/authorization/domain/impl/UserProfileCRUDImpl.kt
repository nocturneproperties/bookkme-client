package me.bookk.feature.authorization.domain.impl

import me.bookk.feature.authorization.domain.api.UserProfileCRUD
import me.bookk.feature.authorization.domain.datasource.profile.UserProfileDataSource
import me.bookk.feature.authorization.domain.entity.UserProfile
import kotlin.uuid.Uuid

internal class UserProfileCRUDImpl(
    private val userProfileDataSource: UserProfileDataSource
) : UserProfileCRUD {

    override suspend fun updateFromRemote() {
        val user = userProfileDataSource.getProfileFromBackend()
        userProfileDataSource.upsertProfile(user)
    }

    override suspend fun get(): UserProfile {
        val local = userProfileDataSource.getProfileFromDatabase()
        return local ?: userProfileDataSource.getProfileFromBackend().also {
            userProfileDataSource.upsertProfile(it)
        }
    }

    override suspend fun update(profile: UserProfile) {
        userProfileDataSource.updateProfile(profile)
        userProfileDataSource.getProfileFromBackend().also {
            userProfileDataSource.updateProfile(it)
        }
    }

    override suspend fun delete(id: Uuid) {
        userProfileDataSource.deleteProfile(id)
    }
}