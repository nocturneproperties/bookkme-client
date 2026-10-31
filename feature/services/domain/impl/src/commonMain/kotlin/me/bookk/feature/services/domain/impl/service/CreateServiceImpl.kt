package me.bookk.feature.services.domain.impl.service

import me.bookk.feature.services.domain.api.service.CreateService
import me.bookk.feature.services.domain.api.service.entity.Service
import me.bookk.feature.services.domain.datasource.ServiceDataSource

internal class CreateServiceImpl(
    private val dataSource: ServiceDataSource
) : CreateService {
    override suspend fun invoke(service: Service): Service {
        return dataSource.createService(service.copy(name = service.name.trim())).also {
            dataSource.saveServiceInDB(it)
        }
    }
}