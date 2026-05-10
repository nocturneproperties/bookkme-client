package me.bookk.feature.services.domain.impl.group

import me.bookk.feature.services.domain.api.group.CreateServiceGroup
import me.bookk.feature.services.domain.api.group.entity.ServiceGroup
import me.bookk.feature.services.domain.datasource.ServiceGroupDataSource

internal class CreateServiceGroupImpl(
    private val dataSource: ServiceGroupDataSource
) : CreateServiceGroup {
    override suspend fun invoke(group: ServiceGroup): ServiceGroup {
        return dataSource.createServiceGroup(group).also {
            dataSource.saveGroupInDB(it)
        }
    }
}