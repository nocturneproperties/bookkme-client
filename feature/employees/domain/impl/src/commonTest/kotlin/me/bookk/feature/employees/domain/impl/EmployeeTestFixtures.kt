package me.bookk.feature.employees.domain.impl

import kotlinx.datetime.LocalDateTime
import me.bookk.feature.business.domain.api.entity.WorkingSchedule
import me.bookk.feature.employees.domain.api.entity.Employee
import me.bookk.feature.employees.domain.api.entity.EmployeeInvitation
import me.bookk.feature.employees.domain.api.entity.EmployeeInvitationStatus
import kotlin.time.Instant
import kotlin.uuid.Uuid

internal fun stubEmployee(businessId: Uuid = Uuid.random(), id: Uuid = Uuid.random()) = Employee(
    id = id,
    businessId = businessId,
    name = "Jane",
    lastName = "Doe",
    phone = "123456789",
    email = "jane@example.com",
    userId = Uuid.random(),
    services = emptyList(),
    schedule = WorkingSchedule(),
    createdAt = Instant.fromEpochMilliseconds(0)
)

internal fun stubEmployeeInvitation(businessId: Uuid = Uuid.random()) = EmployeeInvitation(
    id = Uuid.random(),
    businessId = businessId,
    invitedBy = Uuid.random(),
    code = "ABCD1234",
    status = EmployeeInvitationStatus.PENDING,
    createdAt = LocalDateTime(2024, 1, 1, 0, 0)
)
