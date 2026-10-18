package me.bookk.feature.clients.domain.api

import kotlinx.coroutines.flow.Flow
import me.bookk.feature.clients.domain.api.entity.Client
import kotlin.uuid.Uuid

interface GetClient {
    fun flow(id: Uuid): Flow<Client?>

    suspend operator fun invoke(id: Uuid): Client
}
