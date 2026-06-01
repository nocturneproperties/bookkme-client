package me.bookk.feature.services.domain.api.group

import me.bookk.feature.services.domain.api.group.entity.ServiceGroup
import kotlin.uuid.Uuid

interface GetServiceGroups {
    suspend operator fun invoke(businessId: Uuid): List<ServiceGroup>
    suspend fun cached(businessId: Uuid, onResultAvailable: suspend (List<ServiceGroup>) -> Unit)
}