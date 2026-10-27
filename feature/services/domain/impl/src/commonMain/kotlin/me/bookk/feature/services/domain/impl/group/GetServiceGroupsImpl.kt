package me.bookk.feature.services.domain.impl.group

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import me.bookk.core.coroutine.flatMapLatestOrNull
import me.bookk.feature.business.domain.api.business.ObserveDashboardBusinessChanges
import me.bookk.feature.services.domain.api.group.GetServiceGroups
import me.bookk.feature.services.domain.api.group.entity.ServiceGroup
import me.bookk.feature.services.domain.datasource.ServiceGroupDataSource
import kotlin.uuid.Uuid

internal class GetServiceGroupsImpl(
    private val dataSource: ServiceGroupDataSource,
    private val observeDashboardBusinessChanges: ObserveDashboardBusinessChanges
) : GetServiceGroups {

    override fun flow(): Flow<List<ServiceGroup>> {
        return observeDashboardBusinessChanges()
            .flatMapLatestOrNull { business -> dataSource.observeServiceGroupsDBChanges(business.id) }
            .map { it.orEmpty().sortedBy { group -> group.createdAt } }
    }

    override suspend fun refresh(businessId: Uuid): List<ServiceGroup> {
        val groups = dataSource.getServiceGroups(businessId)
        val freshIds = groups.map { it.id }.toSet()
        val staleIds = dataSource.getServiceGroupIdsInDb(businessId).filterNot { it in freshIds }
        if (staleIds.isNotEmpty()) {
            dataSource.deleteGroupsInDB(staleIds)
        }
        dataSource.saveGroupsInDB(groups)
        dataSource.saveLastSyncedAt(businessId)
        return groups.sortedBy { it.createdAt }
    }
}
