package me.bookk.feature.employees.domain.api

import kotlinx.coroutines.flow.Flow
import me.bookk.feature.employees.domain.api.entity.Employee
import kotlin.uuid.Uuid

interface GetEmployees {
    fun flow(): Flow<List<Employee>>
    suspend fun refresh(businessId: Uuid): List<Employee>
}
