package me.bookk.feature.employees.presentation

import kotlinx.datetime.LocalDateTime
import library.money.api.Money
import me.bookk.feature.business.domain.api.entity.BusinessPermissions
import me.bookk.feature.business.domain.api.entity.ResourcePermission
import me.bookk.feature.business.domain.api.entity.WorkingSchedule
import me.bookk.feature.employees.domain.api.entity.Employee
import me.bookk.feature.employees.domain.api.entity.EmployeeInvitation
import me.bookk.feature.employees.domain.api.entity.EmployeeInvitationStatus
import me.bookk.feature.services.domain.api.group.entity.ServiceGroup
import me.bookk.feature.services.domain.api.service.entity.Service
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Instant
import kotlin.uuid.Uuid

internal fun stubEmployee(
    name: String = "Jane",
    lastName: String = "Doe",
    services: List<Service> = emptyList(),
    permissions: BusinessPermissions = stubPermissions(),
    suspendedAt: Instant? = null
) = Employee(
    id = Uuid.random(),
    businessId = Uuid.random(),
    name = name,
    lastName = lastName,
    phone = "123456789",
    email = "jane@example.com",
    userId = Uuid.random(),
    services = services,
    schedule = WorkingSchedule(),
    createdAt = Instant.fromEpochMilliseconds(0),
    permissions = permissions,
    suspendedAt = suspendedAt
)

internal fun stubPermissions(
    business: ResourcePermission = ResourcePermission(),
    employees: ResourcePermission = ResourcePermission(),
    clients: ResourcePermission = ResourcePermission(),
    services: ResourcePermission = ResourcePermission(),
    appointments: ResourcePermission = ResourcePermission()
) = BusinessPermissions(
    business = business,
    employees = employees,
    clients = clients,
    services = services,
    appointments = appointments
)

internal fun stubService(name: String = "Haircut") = Service(
    id = Uuid.random(),
    businessId = Uuid.random(),
    group = ServiceGroup(
        id = Uuid.random(),
        businessId = Uuid.random(),
        name = "Hair",
        createdAt = Instant.fromEpochMilliseconds(0)
    ),
    name = name,
    duration = 30.minutes,
    price = Money(value = 2000, currencyType = Money.SupportedCurrency.EUR),
    isAvailable = true,
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
