package me.bookk.feature.services.domain.impl.group

import me.bookk.feature.services.domain.api.group.DeleteServiceGroup
import me.bookk.feature.services.domain.api.group.ServiceGroupEvent
import me.bookk.feature.services.domain.api.group.entity.ServiceGroup
import me.bookk.feature.services.domain.api.group.serviceGroupEvents
import me.bookk.feature.services.domain.datasource.ServiceGroupDataSource

internal class DeleteServiceGroupImpl(
    private val dataSource: ServiceGroupDataSource
) : DeleteServiceGroup {
    override suspend fun invoke(group: ServiceGroup) {
        dataSource.deleteServiceGroup(group.businessId, group.id)
        dataSource.deleteGroupFromDB(group)
        serviceGroupEvents.emit(ServiceGroupEvent.Deleted(group))
    }
}