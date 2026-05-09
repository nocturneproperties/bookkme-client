package me.bookk.feature.clients.data.mapping

import me.bookk.database.entity.ClientEntity
import me.bookk.feature.clients.data.remote.model.ClientRemote
import me.bookk.feature.clients.domain.api.entity.Client
import kotlin.uuid.Uuid

internal fun Client.toRemote(): ClientRemote {
    return ClientRemote(
        id = id,
        name = name,
        lastName = lastName,
        phone = phone,
        email = email,
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
            email = email,
            businessId = businessId,
        )

        else -> Client.Integrated(
            id = id,
            name = name,
            lastName = lastName,
            phone = phone,
            email = email,
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
        email = email,
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
            email = email,
            businessId = businessId,
        )

        else -> Client.Integrated(
            id = id,
            name = name,
            lastName = lastName,
            phone = phone,
            email = email,
            businessId = businessId,
            userId = userId
        )
    }
}