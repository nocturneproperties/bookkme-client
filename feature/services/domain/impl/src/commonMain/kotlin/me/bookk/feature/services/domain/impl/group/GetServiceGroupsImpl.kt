package me.bookk.feature.services.domain.impl.group

import me.bookk.feature.services.domain.api.group.GetServiceGroups
import me.bookk.feature.services.domain.api.group.entity.ServiceGroup
import me.bookk.feature.services.domain.datasource.ServiceGroupDataSource
import kotlin.uuid.Uuid

internal class GetServiceGroupsImpl(
    private val dataSource: ServiceGroupDataSource
) : GetServiceGroups {
    override suspend fun invoke(businessId: Uuid): List<ServiceGroup> {
        return dataSource.getServiceGroups(businessId).also {
            dataSource.saveGroupsInDB(it)
            dataSource.saveLastSyncedAt(businessId)
        }.sortedBy { it.createdAt }
    }

    override suspend fun cached(
        businessId: Uuid,
        onResultAvailable: suspend (List<ServiceGroup>) -> Unit
    ) {
        if (dataSource.getLastSyncedAt(businessId) != null) {
            onResultAvailable(dataSource.getServiceGroupsFromDb(businessId).sortedBy { it.createdAt })
        }
        onResultAvailable(invoke(businessId))
    }
}