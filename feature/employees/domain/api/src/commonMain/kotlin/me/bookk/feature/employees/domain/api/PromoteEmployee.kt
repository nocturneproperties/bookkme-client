package me.bookk.feature.employees.domain.api

import me.bookk.feature.employees.domain.api.entity.EmployeeRole
import kotlin.uuid.Uuid

interface PromoteEmployee {
    suspend operator fun invoke(businessId: Uuid, id: Uuid, role: EmployeeRole)
}
