package me.bookk.feature.employees.domain.api

import me.bookk.feature.business.domain.api.entity.BusinessPermissions
import me.bookk.feature.business.domain.api.entity.BusinessResource
import me.bookk.feature.business.domain.api.entity.ResourcePermission
import kotlin.uuid.Uuid

interface SetEmployeePermission {
    suspend operator fun invoke(
        businessId: Uuid,
        id: Uuid,
        resource: BusinessResource,
        permission: ResourcePermission
    ): BusinessPermissions

    sealed interface Error {
        class InsufficientGrant(cause: Throwable) : Error, Throwable(cause)
    }
}
