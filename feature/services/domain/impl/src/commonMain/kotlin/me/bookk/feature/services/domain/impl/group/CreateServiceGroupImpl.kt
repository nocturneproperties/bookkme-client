package me.bookk.feature.services.domain.impl.group

import me.bookk.core.domain.entity.onBusinessError
import me.bookk.feature.services.domain.api.group.CreateServiceGroup
import me.bookk.feature.services.domain.api.group.CreateServiceGroup.Error
import me.bookk.feature.services.domain.api.group.ServiceGroupEvent
import me.bookk.feature.services.domain.api.group.entity.ServiceGroup
import me.bookk.feature.services.domain.api.group.serviceGroupEvents
import me.bookk.feature.services.domain.datasource.ServiceErrorCodes
import me.bookk.feature.services.domain.datasource.ServiceGroupDataSource

internal class CreateServiceGroupImpl(
    private val dataSource: ServiceGroupDataSource
) : CreateServiceGroup {
    override suspend fun invoke(group: ServiceGroup): ServiceGroup = runCatching {
        dataSource.createServiceGroup(group).also {
            dataSource.saveGroupInDB(it)
            serviceGroupEvents.emit(ServiceGroupEvent.Created(it))
        }
    }.onBusinessError { error ->
        when (error.errorCode) {
            ServiceErrorCodes.BUSINESS_SERVICE_GROUP_EXISTS -> throw Error.NameExists(error)
            ServiceErrorCodes.BUSINESS_SERVICE_GROUP_VALIDATION_ERROR -> throw Error.InvalidName(error)
        }
    }.getOrThrow()
}