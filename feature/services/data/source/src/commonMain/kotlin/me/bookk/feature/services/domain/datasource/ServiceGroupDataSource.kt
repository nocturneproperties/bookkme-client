package me.bookk.feature.services.domain.datasource

import kotlinx.coroutines.flow.Flow
import me.bookk.feature.services.domain.api.group.entity.ServiceGroup
import kotlin.time.Instant
import kotlin.uuid.Uuid

interface ServiceGroupDataSource {
    suspend fun getServiceGroups(businessId: Uuid): List<ServiceGroup>
    suspend fun createServiceGroup(group: ServiceGroup): ServiceGroup
    suspend fun deleteServiceGroup(businessId: Uuid, id: Uuid)

    suspend fun saveGroupsInDB(groups: List<ServiceGroup>)
    suspend fun saveGroupInDB(group: ServiceGroup)
    suspend fun deleteGroupFromDB(group: ServiceGroup)
    suspend fun getServiceGroupIdsInDb(businessId: Uuid): List<Uuid>
    suspend fun deleteGroupsInDB(ids: List<Uuid>)
    fun observeServiceGroupsDBChanges(businessId: Uuid): Flow<List<ServiceGroup>>
    suspend fun getLastSyncedAt(businessId: Uuid): Instant?
    suspend fun saveLastSyncedAt(businessId: Uuid)
}