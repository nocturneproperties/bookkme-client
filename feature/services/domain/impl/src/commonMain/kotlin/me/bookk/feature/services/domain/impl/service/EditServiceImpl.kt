package me.bookk.feature.services.domain.impl.service

import me.bookk.feature.services.domain.api.service.EditService
import me.bookk.feature.services.domain.api.service.entity.Service
import me.bookk.feature.services.domain.datasource.ServiceDataSource

internal class EditServiceImpl(
    private val dataSource: ServiceDataSource
) : EditService {
    override suspend fun invoke(service: Service): Service {
        return dataSource.editService(service).also {
            dataSource.saveServiceInDB(it)
        }
    }
}