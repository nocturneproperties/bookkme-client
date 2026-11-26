package me.bookk.feature.appointments.domain.impl

import me.bookk.feature.business.domain.api.entity.Business
import kotlin.uuid.Uuid

internal fun List<Business>.names(): Map<Uuid, String> {
    return associate { it.id to it.name }
}
