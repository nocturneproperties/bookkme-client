package me.bookk.feature.services.data.datasource

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.resources.delete
import io.ktor.client.plugins.resources.get
import io.ktor.client.plugins.resources.post
import io.ktor.client.request.setBody
import library.cache.api.PreferenceProvider
import library.cache.api.Preferences
import library.cache.api.get
import library.cache.api.set
import me.bookk.core.data.DataSource
import me.bookk.core.domain.logout.LogOutAction
import me.bookk.database.dao.ServiceGroupDao
import me.bookk.feature.services.data.mapper.toDb
import me.bookk.feature.services.data.mapper.toDomain
import me.bookk.feature.services.data.remote.api.ServiceRouting.Api
import me.bookk.feature.services.data.remote.model.ServiceGroupRemote
import me.bookk.feature.services.domain.api.group.entity.ServiceGroup
import me.bookk.feature.services.domain.datasource.ServiceGroupDataSource
import kotlin.time.Clock
import kotlin.time.Instant
import kotlin.uuid.Uuid

internal class ServiceGroupDataSourceImpl(
    private val httpClient: HttpClient,
    private val serviceGroupDao: ServiceGroupDao,
    preferenceProvider: PreferenceProvider
) : DataSource(), ServiceGroupDataSource, LogOutAction {

    private val preferences = preferenceProvider.get("service_groups_prefs")
    override suspend fun getServiceGroups(businessId: Uuid): List<ServiceGroup> = mapExceptions {
        httpClient.get(Api.ServiceGroup(businessId = businessId))
            .body<List<ServiceGroupRemote>>()
            .map { it.toDomain() }
    }

    override suspend fun createServiceGroup(group: ServiceGroup): ServiceGroup = mapExceptions {
        httpClient.post(Api.ServiceGroup(businessId = group.businessId)) {
            setBody(ServiceGroupRemote.fromDomain(group))
        }
            .body<ServiceGroupRemote>()
            .toDomain()
    }

    override suspend fun deleteServiceGroup(businessId: Uuid, id: Uuid) {
        mapExceptions {
            httpClient.delete(Api.ServiceGroup.Id(Api.ServiceGroup(businessId = businessId), id))
        }
    }

    override suspend fun saveGroupsInDB(groups: List<ServiceGroup>) = mapExceptions {
        serviceGroupDao.upsert(groups.map { it.toDb() })
    }

    override suspend fun saveGroupInDB(group: ServiceGroup) = mapExceptions {
        serviceGroupDao.upsert(group.toDb())
    }

    override suspend fun deleteGroupFromDB(group: ServiceGroup) = mapExceptions {
        serviceGroupDao.delete(group.toDb())
    }

    override suspend fun getServiceGroupsFromDb(businessId: Uuid): List<ServiceGroup> = mapExceptions {
        serviceGroupDao.get(businessId).map { it.toDomain() }
    }

    override suspend fun getLastSyncedAt(businessId: Uuid): Instant? {
        return preferences.get(Key.lastSyncedAt(businessId))?.let { Instant.fromEpochMilliseconds(it) }
    }

    override suspend fun saveLastSyncedAt(businessId: Uuid) {
        preferences.set(Key.lastSyncedAt(businessId), Clock.System.now().toEpochMilliseconds())
    }

    override suspend fun doOnLogOut() {
        preferences.clear()
        serviceGroupDao.clear()
    }

    private object Key {
        fun lastSyncedAt(businessId: Uuid) = Preferences.Key<Long>("last_synced_at_$businessId")
    }
}