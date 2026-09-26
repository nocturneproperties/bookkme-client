package me.bookk.feature.services.domain.api.group

import kotlinx.coroutines.flow.Flow
import me.bookk.feature.services.domain.api.group.entity.ServiceGroup
import kotlin.uuid.Uuid

interface GetServiceGroups {
    fun flow(): Flow<List<ServiceGroup>>
    suspend fun refresh(businessId: Uuid): List<ServiceGroup>
}