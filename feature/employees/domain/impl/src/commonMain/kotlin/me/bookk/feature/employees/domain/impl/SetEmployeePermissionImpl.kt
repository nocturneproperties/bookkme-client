package me.bookk.feature.employees.domain.impl

import me.bookk.core.domain.entity.onBusinessError
import me.bookk.feature.business.domain.api.entity.BusinessPermissions
import me.bookk.feature.business.domain.api.entity.BusinessResource
import me.bookk.feature.business.domain.api.entity.ResourcePermission
import me.bookk.feature.employees.domain.api.SetEmployeePermission
import me.bookk.feature.employees.domain.api.SetEmployeePermission.Error
import me.bookk.feature.employees.domain.datasource.EmployeeDataSource
import me.bookk.feature.employees.domain.datasource.EmployeeErrorCodes
import kotlin.uuid.Uuid

internal class SetEmployeePermissionImpl(
    private val dataSource: EmployeeDataSource
) : SetEmployeePermission {
    override suspend fun invoke(
        businessId: Uuid,
        id: Uuid,
        resource: BusinessResource,
        permission: ResourcePermission
    ): BusinessPermissions = runCatching {
        dataSource.setEmployeePermission(businessId, id, resource, permission)
    }.onBusinessError { error ->
        when (error.errorCode) {
            EmployeeErrorCodes.BUSINESS_INSUFFICIENT_GRANT_PERMISSION -> throw Error.InsufficientGrant(error)
        }
    }.getOrThrow()
}
