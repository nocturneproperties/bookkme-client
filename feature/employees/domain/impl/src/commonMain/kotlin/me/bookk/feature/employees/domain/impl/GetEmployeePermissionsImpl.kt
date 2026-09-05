package me.bookk.feature.employees.domain.impl

import me.bookk.feature.business.domain.api.entity.BusinessPermissions
import me.bookk.feature.employees.domain.api.GetEmployeePermissions
import me.bookk.feature.employees.domain.datasource.EmployeeDataSource
import kotlin.uuid.Uuid

internal class GetEmployeePermissionsImpl(
    private val dataSource: EmployeeDataSource
) : GetEmployeePermissions {
    override suspend fun invoke(businessId: Uuid, id: Uuid): BusinessPermissions {
        return dataSource.getEmployeePermissions(businessId, id)
    }
}
