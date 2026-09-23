package me.bookk.feature.services.domain.impl.service

import me.bookk.feature.services.domain.api.service.DeleteService
import me.bookk.feature.services.domain.api.service.entity.Service
import me.bookk.feature.services.domain.datasource.ServiceDataSource

internal class DeleteServiceImpl(
    private val dataSource: ServiceDataSource
) : DeleteService {
    override suspend fun invoke(service: Service) {
        dataSource.deleteService(service.businessId, service.id)
        dataSource.deleteServiceFromDB(service)
    }
}