package me.bookk.feature.services.domain.api.service

import kotlinx.coroutines.flow.Flow
import me.bookk.feature.services.domain.api.service.entity.Service
import kotlin.uuid.Uuid

interface GetBusinessServices {
    fun flow(businessId: Uuid): Flow<List<Service>>
    suspend fun refresh(businessId: Uuid): List<Service>
}
