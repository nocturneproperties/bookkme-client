package me.bookk.feature.employees.domain.api.entity

import me.bookk.feature.business.domain.api.entity.WorkingSchedule
import me.bookk.feature.services.domain.api.service.entity.Service
import kotlin.time.Instant
import kotlin.uuid.Uuid

data class Employee(
    val id: Uuid,
    val businessId: Uuid,
    val name: String,
    val lastName: String,
    val phone: String?,
    val email: String?,
    val userId: Uuid,
    val services: List<Service>,
    val schedule: WorkingSchedule,
    val createdAt: Instant
) {
    val fullName: String
        get() = "$name $lastName"
}

enum class EmployeeRole {
    EMPLOYEE,
    MANAGER
}
