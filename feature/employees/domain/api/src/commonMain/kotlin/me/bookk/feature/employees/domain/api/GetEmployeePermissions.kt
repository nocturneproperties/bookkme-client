package me.bookk.feature.employees.domain.api

import me.bookk.feature.business.domain.api.entity.BusinessPermissions
import kotlin.uuid.Uuid

interface GetEmployeePermissions {
    suspend operator fun invoke(businessId: Uuid, id: Uuid): BusinessPermissions
}
