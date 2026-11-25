package me.bookk.feature.appointments.domain.impl

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import me.bookk.feature.business.domain.api.business.GetBusinesses
import me.bookk.feature.business.domain.api.entity.Business
import kotlin.uuid.Uuid

internal fun List<Business>.names(): Map<Uuid, String> {
    return associate { it.id to it.name }
}

internal fun GetBusinesses.nameOf(businessId: Uuid): Flow<String> {
    return flow(setOf(businessId))
        .map { businesses -> businesses.names()[businessId].orEmpty() }
}
