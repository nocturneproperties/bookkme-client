package me.bookk.feature.business.domain.api.business

import kotlinx.coroutines.flow.Flow
import me.bookk.feature.business.domain.api.entity.Business
import kotlin.uuid.Uuid

interface GetBusinesses {
    fun flow(ids: Set<Uuid>): Flow<List<Business>>
    suspend fun refresh(ids: Set<Uuid>): List<Business>
}
