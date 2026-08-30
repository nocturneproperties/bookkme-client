package me.bookk.feature.employees.domain.api

import me.bookk.feature.employees.domain.api.entity.Employee
import kotlin.uuid.Uuid

interface GetEmployees {
    suspend operator fun invoke(businessId: Uuid): List<Employee>
}
