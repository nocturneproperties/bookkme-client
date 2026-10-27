package me.bookk.feature.employees.domain.api

import kotlinx.coroutines.flow.Flow
import me.bookk.feature.employees.domain.api.entity.EmployeeInvitation
import kotlin.uuid.Uuid

interface GetEmployeeInvitations {
    fun flow(businessId: Uuid): Flow<List<EmployeeInvitation>>
    suspend fun refresh(businessId: Uuid): List<EmployeeInvitation>
}
