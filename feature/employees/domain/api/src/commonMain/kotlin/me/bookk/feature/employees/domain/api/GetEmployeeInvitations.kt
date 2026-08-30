package me.bookk.feature.employees.domain.api

import me.bookk.feature.employees.domain.api.entity.EmployeeInvitation
import kotlin.uuid.Uuid

interface GetEmployeeInvitations {
    suspend operator fun invoke(businessId: Uuid): List<EmployeeInvitation>
}
