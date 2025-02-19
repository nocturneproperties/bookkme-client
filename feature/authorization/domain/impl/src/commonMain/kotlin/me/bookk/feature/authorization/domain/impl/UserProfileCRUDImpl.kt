package me.bookk.feature.authorization.domain.impl

import me.bookk.feature.authorization.domain.api.UserProfileCRUD
import me.bookk.feature.authorization.domain.datasource.profile.UserProfileDataSource
import me.bookk.feature.authorization.domain.entity.UserProfile

internal class UserProfileCRUDImpl(
    private val userProfileDataSource: UserProfileDataSource
) : UserProfileCRUD {
    override suspend fun get(): UserProfile {
        val local = userProfileDataSource.getProfileFromDatabase()
        return local ?: userProfileDataSource.getProfileFromBackend().also {
            userProfileDataSource.insertProfile(it)
        }
    }

    override suspend fun update(profile: UserProfile) {
        userProfileDataSource.updateProfile(profile)
        userProfileDataSource.getProfileFromBackend().also {
            userProfileDataSource.updateProfile(it)
        }
    }

    override suspend fun delete(id: Long) {
        userProfileDataSource.deleteProfile(id)
    }
}