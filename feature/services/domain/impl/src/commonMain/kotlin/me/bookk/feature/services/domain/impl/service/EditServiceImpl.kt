package me.bookk.feature.services.domain.impl.service

import me.bookk.core.domain.entity.onBusinessError
import me.bookk.feature.services.domain.datasource.ServiceErrorCodes
import me.bookk.feature.services.domain.api.service.EditService
import me.bookk.feature.services.domain.api.service.entity.Service
import me.bookk.feature.services.domain.datasource.ServiceDataSource

internal class EditServiceImpl(
    private val dataSource: ServiceDataSource
) : EditService {
    override suspend fun invoke(service: Service): Service {
        return runCatching {
            dataSource.editService(service.copy(name = service.name.trim())).also {
                dataSource.saveServiceInDB(it)
            }
        }.onBusinessError {
            when (it.errorCode) {
                ServiceErrorCodes.BUSINESS_SERVICE_EXISTS -> throw EditService.Error.NameAlreadyExists()
                ServiceErrorCodes.BUSINESS_SERVICE_NAME_VALIDATION_ERROR -> throw EditService.Error.InvalidName()
                ServiceErrorCodes.BUSINESS_SERVICE_GROUP_NOT_EXISTS -> throw EditService.Error.GroupNotFound()
                ServiceErrorCodes.BUSINESS_SERVICE_PRICE_PRECISION_INVALID -> throw EditService.Error.InvalidPricePrecision()
                ServiceErrorCodes.BUSINESS_SERVICE_BUSINESS_CURRENCY_INVALID -> throw EditService.Error.InvalidBusinessCurrency()
            }
        }.getOrThrow()
    }
}