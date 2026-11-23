package me.bookk.feature.employees.domain.impl

import kotlinx.coroutines.flow.Flow
import me.bookk.feature.employees.domain.api.GetBookableEmployees
import me.bookk.feature.employees.domain.api.entity.BookableEmployee
import me.bookk.feature.employees.domain.datasource.BookableEmployeeDataSource
import kotlin.uuid.Uuid

internal class GetBookableEmployeesImpl(
    private val dataSource: BookableEmployeeDataSource,
) : GetBookableEmployees {

    override fun flow(businessId: Uuid): Flow<List<BookableEmployee>> {
        return dataSource.observeBookableEmployeesInDb(businessId)
    }

    override suspend fun refresh(businessId: Uuid): List<BookableEmployee> {
        val employees = dataSource.getBookableEmployees(businessId)
        dataSource.replaceBookableEmployeesInDb(businessId, employees)
        return employees
    }
}
