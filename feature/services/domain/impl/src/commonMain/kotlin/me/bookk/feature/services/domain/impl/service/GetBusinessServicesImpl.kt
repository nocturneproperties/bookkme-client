package me.bookk.feature.services.domain.impl.service

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import me.bookk.feature.services.domain.api.service.GetBusinessServices
import me.bookk.feature.services.domain.api.service.entity.Service
import me.bookk.feature.services.domain.datasource.ServiceDataSource
import me.bookk.feature.services.domain.datasource.ServiceGroupDataSource
import kotlin.uuid.Uuid

internal class GetBusinessServicesImpl(
    private val dataSource: ServiceDataSource,
    private val groupsDataSource: ServiceGroupDataSource,
) : GetBusinessServices {

    override fun flow(businessId: Uuid): Flow<List<Service>> {
        return dataSource.observeServicesDBChanges(businessId)
            .map { services -> services.sortedBy { it.createdAt } }
    }

    override suspend fun refresh(businessId: Uuid): List<Service> {
        val services = dataSource.getServices(businessId)
        deleteStaleServices(businessId, services)
        groupsDataSource.saveGroupsInDB(services.map { it.group }.distinctBy { it.id })
        dataSource.saveServicesInDB(services)
        dataSource.saveLastSyncedAt(businessId)
        return services.sortedBy { it.createdAt }
    }

    private suspend fun deleteStaleServices(businessId: Uuid, services: List<Service>) {
        val freshIds = services.map { it.id }.toSet()
        val staleIds = dataSource.getServiceIdsInDb(businessId).filterNot { it in freshIds }
        if (staleIds.isNotEmpty()) {
            dataSource.deleteServicesInDb(staleIds)
        }
    }
}
