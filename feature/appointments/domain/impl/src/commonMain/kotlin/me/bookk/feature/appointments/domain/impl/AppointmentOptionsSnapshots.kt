package me.bookk.feature.appointments.domain.impl

import me.bookk.feature.appointments.domain.api.entity.ClientSnapshot
import me.bookk.feature.appointments.domain.api.entity.ServiceSnapshot
import me.bookk.feature.clients.domain.api.entity.Client
import me.bookk.feature.services.domain.api.service.entity.Service

internal fun List<Service>.snapshotServices(): List<ServiceSnapshot> {
    return map {
        ServiceSnapshot(
            id = it.id,
            name = it.name,
            groupId = it.group.id,
            price = it.price,
            duration = it.duration,
        )
    }
}

internal fun Client.snapshot(): ClientSnapshot {
    return ClientSnapshot(
        id = id,
        fullName = fullName,
        phone = phone,
        email = email,
    )
}
