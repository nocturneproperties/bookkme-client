package me.bookk.feature.authorization.data.datasource

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.resources.get
import io.ktor.client.plugins.resources.put
import io.ktor.client.request.setBody
import me.bookk.core.data.DataSource
import me.bookk.database.dao.UserProfileDao
import me.bookk.feature.authorization.data.mapping.toDb
import me.bookk.feature.authorization.data.mapping.toDomain
import me.bookk.feature.authorization.data.mapping.toRemote
import me.bookk.feature.authorization.data.remote.api.AuthRouting
import me.bookk.feature.authorization.data.remote.api.UserRouting
import me.bookk.feature.authorization.data.remote.model.UserProfileRemote
import me.bookk.feature.authorization.domain.datasource.profile.UserProfileDataSource
import me.bookk.feature.authorization.domain.entity.UserProfile

internal class CommonUserProfileDataSource(
    private val profileDao: UserProfileDao,
    private val httpClient: HttpClient
) : DataSource(), UserProfileDataSource {
    override suspend fun getProfileFromDatabase(): UserProfile? = mapExceptions {
        profileDao.queryProfile()?.toDomain()
    }

    override suspend fun getProfileFromBackend(): UserProfile = mapExceptions {
        httpClient.get(AuthRouting.Api.User.Me())
            .body<UserProfileRemote>()
            .toDomain()
    }

    override suspend fun insertProfile(profile: UserProfile) = mapExceptions {
        profileDao.insert(profile.toDb())
    }

    override suspend fun updateProfile(userProfile: UserProfile) = mapExceptions {
        httpClient.put(UserRouting.Api.User.Me) {
            setBody(userProfile.toRemote())
        }
        profileDao.update(userProfile.toDb())
    }

    override suspend fun deleteProfile(id: Long) = mapExceptions {
        TODO("Not yet implemented")
    }
}