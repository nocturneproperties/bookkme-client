package me.bookk.feature.services.domain.datasource

import me.bookk.feature.services.domain.api.group.entity.ServiceGroup
import kotlin.uuid.Uuid

interface ServiceGroupDataSource {
    suspend fun getServiceGroups(businessId: Uuid): List<ServiceGroup>
    suspend fun createServiceGroup(group: ServiceGroup): ServiceGroup
    suspend fun deleteServiceGroup(businessId: Uuid, id: Uuid)

    suspend fun saveGroupsInDB(groups: List<ServiceGroup>)
    suspend fun saveGroupInDB(group: ServiceGroup)
    suspend fun deleteGroupFromDB(group: ServiceGroup)
    suspend fun getServiceGroupsFromDb(businessId: Uuid): List<ServiceGroup>
}