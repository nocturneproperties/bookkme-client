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
        }
    }

    override suspend fun cached(
        businessId: Uuid,
        onResultAvailable: suspend (List<Service>) -> Unit
    ) {
        val groups = dataSource.getServicesFromDb(businessId)
        if (groups.isNotEmpty()) {
            onResultAvailable(groups)
        }
        onResultAvailable(invoke(businessId))
    }
}