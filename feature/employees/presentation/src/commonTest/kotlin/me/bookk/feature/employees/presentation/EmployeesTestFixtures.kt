package me.bookk.feature.employees.presentation

import kotlinx.datetime.LocalDateTime
import me.bookk.feature.business.domain.api.entity.WorkingSchedule
import me.bookk.feature.employees.domain.api.entity.Employee
import me.bookk.feature.employees.domain.api.entity.EmployeeInvitation
import me.bookk.feature.employees.domain.api.entity.EmployeeInvitationStatus
import kotlin.time.Instant
import kotlin.uuid.Uuid

internal fun stubEmployee(name: String = "Jane", lastName: String = "Doe") = Employee(
    id = Uuid.random(),
    businessId = Uuid.random(),
    name = name,
    lastName = lastName,
    phone = "123456789",
    email = "jane@example.com",
    userId = Uuid.random(),
    services = emptyList(),
    schedule = WorkingSchedule(),
    createdAt = Instant.fromEpochMilliseconds(0)
)

internal fun stubInvitation(
    status: EmployeeInvitationStatus = EmployeeInvitationStatus.PENDING,
    createdAt: LocalDateTime = LocalDateTime(2024, 1, 1, 0, 0),
    code: String? = null
) = EmployeeInvitation(
    id = Uuid.random(),
    businessId = Uuid.random(),
    invitedBy = Uuid.random(),
    code = code,
    status = status,
    createdAt = createdAt
)
