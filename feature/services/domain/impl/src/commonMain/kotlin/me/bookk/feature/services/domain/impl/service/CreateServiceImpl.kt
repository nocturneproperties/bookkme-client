package me.bookk.feature.services.domain.impl.service

import me.bookk.core.domain.entity.onBusinessError
import me.bookk.feature.services.domain.datasource.ServiceErrorCodes
import me.bookk.feature.services.domain.api.service.CreateService
import me.bookk.feature.services.domain.api.service.entity.Service
import me.bookk.feature.services.domain.datasource.ServiceDataSource

internal class CreateServiceImpl(
    private val dataSource: ServiceDataSource
) : CreateService {
    override suspend fun invoke(service: Service): Service {
        return runCatching {
            dataSource.createService(service.copy(name = service.name.trim())).also {
                dataSource.saveServiceInDB(it)
            }
        }.onBusinessError {
            when (it.errorCode) {
                ServiceErrorCodes.BUSINESS_SERVICE_EXISTS -> throw CreateService.Error.NameAlreadyExists()
                ServiceErrorCodes.BUSINESS_SERVICE_NAME_VALIDATION_ERROR -> throw CreateService.Error.InvalidName()
                ServiceErrorCodes.BUSINESS_SERVICE_GROUP_NOT_EXISTS -> throw CreateService.Error.GroupNotFound()
                ServiceErrorCodes.BUSINESS_SERVICE_PRICE_PRECISION_INVALID -> throw CreateService.Error.InvalidPricePrecision()
                ServiceErrorCodes.BUSINESS_SERVICE_BUSINESS_CURRENCY_INVALID -> throw CreateService.Error.InvalidBusinessCurrency()
            }
        }.getOrThrow()
    }
}