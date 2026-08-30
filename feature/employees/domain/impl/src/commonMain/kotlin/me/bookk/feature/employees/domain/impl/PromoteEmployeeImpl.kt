package me.bookk.feature.employees.domain.impl

import me.bookk.feature.employees.domain.api.PromoteEmployee
import me.bookk.feature.employees.domain.api.entity.EmployeeRole
import me.bookk.feature.employees.domain.datasource.EmployeeDataSource
import kotlin.uuid.Uuid

internal class PromoteEmployeeImpl(
    private val dataSource: EmployeeDataSource
) : PromoteEmployee {
    override suspend fun invoke(businessId: Uuid, id: Uuid, role: EmployeeRole) {
        dataSource.promoteEmployee(businessId, id, role)
    }
}
