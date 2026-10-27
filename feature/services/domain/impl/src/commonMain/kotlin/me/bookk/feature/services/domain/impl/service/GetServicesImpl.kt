package me.bookk.feature.services.domain.impl.service

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import me.bookk.core.coroutine.flatMapLatestOrNull
import me.bookk.feature.business.domain.api.business.ObserveDashboardBusinessChanges
import me.bookk.feature.services.domain.api.service.GetServices
import me.bookk.feature.services.domain.api.service.entity.Service
import me.bookk.feature.services.domain.datasource.ServiceDataSource
import me.bookk.feature.services.domain.datasource.ServiceGroupDataSource
import kotlin.uuid.Uuid

internal class GetServicesImpl(
    private val dataSource: ServiceDataSource,
    private val groupsDataSource: ServiceGroupDataSource,
    private val observeDashboardBusinessChanges: ObserveDashboardBusinessChanges
) : GetServices {

    override fun flow(): Flow<List<Service>> {
        return observeDashboardBusinessChanges()
            .flatMapLatestOrNull { business -> dataSource.observeServicesDBChanges(business.id) }
            .map { it.orEmpty().sortedBy { service -> service.createdAt } }
    }

    override suspend fun refresh(businessId: Uuid): List<Service> {
        val services = dataSource.getServices(businessId)
        val freshIds = services.map { it.id }.toSet()
        val staleIds = dataSource.getServiceIdsInDb(businessId).filterNot { it in freshIds }
        if (staleIds.isNotEmpty()) {
            dataSource.deleteServicesInDb(staleIds)
        }
        groupsDataSource.saveGroupsInDB(services.map { it.group }.distinctBy { it.id })
        dataSource.saveServicesInDB(services)
        dataSource.saveLastSyncedAt(businessId)
        return services.sortedBy { it.createdAt }
    }
}
