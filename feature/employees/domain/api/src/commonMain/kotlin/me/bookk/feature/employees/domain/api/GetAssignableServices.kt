package me.bookk.feature.employees.domain.api

import kotlinx.coroutines.flow.Flow
import me.bookk.feature.services.domain.api.service.entity.Service
import kotlin.uuid.Uuid

interface GetAssignableServices {
    fun flow(): Flow<List<Service>>
    suspend fun refresh(businessId: Uuid): List<Service>
}
