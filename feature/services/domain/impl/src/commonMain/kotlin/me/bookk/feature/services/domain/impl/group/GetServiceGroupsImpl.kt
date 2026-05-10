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
        }
    }

    override suspend fun cached(
        businessId: Uuid,
        onResultAvailable: suspend (List<ServiceGroup>) -> Unit
    ) {
        val groups = dataSource.getServiceGroups(businessId)
        if (groups.isNotEmpty()) {
            onResultAvailable(groups)
        }
        onResultAvailable(invoke(businessId))
    }
}