package me.bookk.feature.business.data.mapping

import me.bookk.database.entity.ClientEntity
import me.bookk.feature.business.data.remote.model.ClientRemote
import me.bookk.feature.business.domain.api.entity.Client
import kotlin.uuid.Uuid

internal fun Client.toRemote(): ClientRemote {
    return ClientRemote(
        id = id,
        name = name,
        lastName = lastName,
        phone = phone,
        userId = (this as? Client.Integrated)?.userId
    )
}

internal fun ClientRemote.toDomain(businessId: Uuid): Client {
    return when (val userId = userId) {
        null -> Client.Detached(
            id = id,
            name = name,
            lastName = lastName,
            phone = phone,
            businessId = businessId,
        )

        else -> Client.Integrated(
            id = id,
            name = name,
            lastName = lastName,
            phone = phone,
            businessId = businessId,
            userId = userId
        )
    }
}

internal fun Client.toDbEntity(): ClientEntity {
    return ClientEntity(
        id = id,
        name = name,
        lastName = lastName,
        phone = phone,
        businessId = businessId,
        userId = (this as? Client.Integrated)?.userId
    )
}

internal fun ClientEntity.toDomain(): Client {
    return when (val userId = userId) {
        null -> Client.Detached(
            id = id,
            name = name,
            lastName = lastName,
            phone = phone,
            businessId = businessId,
        )

        else -> Client.Integrated(
            id = id,
            name = name,
            lastName = lastName,
            phone = phone,
            businessId = businessId,
            userId = userId
        )
    }
}