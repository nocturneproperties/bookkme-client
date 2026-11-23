package me.bookk.feature.employees.domain.api

import kotlinx.coroutines.flow.Flow
import me.bookk.feature.employees.domain.api.entity.BookableEmployee
import kotlin.uuid.Uuid

interface GetBookableEmployees {
    fun flow(businessId: Uuid): Flow<List<BookableEmployee>>
    suspend fun refresh(businessId: Uuid): List<BookableEmployee>
}
