package me.bookk.feature.clients.domain.api

import kotlinx.coroutines.flow.Flow
import me.bookk.feature.clients.domain.api.entity.Client
import kotlin.uuid.Uuid

interface GetClientsList {
    fun flow(): Flow<List<Client>>
    suspend fun refresh(businessId: Uuid): List<Client>
}
