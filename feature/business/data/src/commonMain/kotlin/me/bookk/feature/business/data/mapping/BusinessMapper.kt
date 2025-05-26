package me.bookk.feature.business.data.mapping

import me.bookk.database.entity.BusinessEntity
import me.bookk.feature.business.data.remote.model.BusinessRemote
import me.bookk.feature.business.domain.api.entity.Business

internal fun BusinessRemote.toDomain(): Business {
    return Business(
        id = id,
        name = name
    )
}

internal fun Business.toLocal(): BusinessEntity {
    return BusinessEntity(
        id = id,
        name = name
    )
}

internal fun BusinessEntity.toDomain(): Business {
    return Business(
        id = id,
        name = name
    )
}