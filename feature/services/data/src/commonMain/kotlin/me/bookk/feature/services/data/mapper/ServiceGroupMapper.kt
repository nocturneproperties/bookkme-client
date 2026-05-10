package me.bookk.feature.services.data.mapper

import me.bookk.database.entity.ServiceGroupEntity
import me.bookk.feature.services.domain.api.group.entity.ServiceGroup

internal fun ServiceGroup.toDb(): ServiceGroupEntity {
    return ServiceGroupEntity(
        id = id,
        businessId = businessId,
        name = name,
    )
}

internal fun ServiceGroupEntity.toDomain(): ServiceGroup {
    return ServiceGroup(
        id = id,
        businessId = businessId,
        name = name,
    )
}
