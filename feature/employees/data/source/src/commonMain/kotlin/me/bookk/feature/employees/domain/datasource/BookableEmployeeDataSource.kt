package me.bookk.feature.employees.domain.datasource

import kotlinx.coroutines.flow.Flow
import me.bookk.feature.employees.domain.api.entity.BookableEmployee
import kotlin.uuid.Uuid

interface BookableEmployeeDataSource {
    suspend fun getBookableEmployees(businessId: Uuid): List<BookableEmployee>
    fun observeBookableEmployeesInDb(businessId: Uuid): Flow<List<BookableEmployee>>
    suspend fun replaceBookableEmployeesInDb(businessId: Uuid, employees: List<BookableEmployee>)
}
