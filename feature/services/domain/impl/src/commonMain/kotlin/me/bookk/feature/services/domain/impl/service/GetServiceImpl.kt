package me.bookk.feature.services.domain.impl.service

import me.bookk.feature.services.domain.api.service.GetService
import me.bookk.feature.services.domain.api.service.entity.Service
import me.bookk.feature.services.domain.datasource.ServiceDataSource
import kotlin.uuid.Uuid

internal class GetServiceImpl(
    private val dataSource: ServiceDataSource
) : GetService {
    override suspend fun invoke(id: Uuid): Service {
        return dataSource.getServiceFromDb(id) ?: throw GetService.Error.NotFound()
    }
}
