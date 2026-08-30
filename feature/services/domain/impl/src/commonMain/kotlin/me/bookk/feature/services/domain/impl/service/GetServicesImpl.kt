package me.bookk.feature.services.domain.impl.service

import me.bookk.feature.services.domain.api.service.GetServices
import me.bookk.feature.services.domain.api.service.entity.Service
import me.bookk.feature.services.domain.datasource.ServiceDataSource
import me.bookk.feature.services.domain.datasource.ServiceGroupDataSource
import kotlin.uuid.Uuid

internal class GetServicesImpl(
    private val dataSource: ServiceDataSource,
    private val groupsDataSource: ServiceGroupDataSource
) : GetServices {
    override suspend fun invoke(businessId: Uuid): List<Service> {
        return dataSource.getServices(businessId).also {
            groupsDataSource.saveGroupsInDB(it.map { it.group }.distinctBy { it.id })
            dataSource.saveServicesInDB(it)
            dataSource.saveLastSyncedAt(businessId)
        }.sortedBy { it.createdAt }
    }

    override suspend fun cached(
        businessId: Uuid,
        onResultAvailable: suspend (List<Service>) -> Unit
    ) {
        if (dataSource.getLastSyncedAt(businessId) != null) {
            onResultAvailable(dataSource.getServicesFromDb(businessId).sortedBy { it.createdAt })
        }
        onResultAvailable(invoke(businessId))
    }
}