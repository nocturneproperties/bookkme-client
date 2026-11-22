package me.bookk.feature.business.data.mapping

import me.bookk.database.entity.BusinessLinkEntity
import me.bookk.feature.business.domain.api.entity.BusinessLink

internal fun BusinessLink.toEntity(): BusinessLinkEntity {
    return BusinessLinkEntity(businessId = businessId, name = name)
}

internal fun BusinessLinkEntity.toDomain(): BusinessLink {
    return BusinessLink(businessId = businessId, name = name)
}
