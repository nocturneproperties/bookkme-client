package me.bookk.feature.employees.domain.impl

import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import library.money.api.Currency
import library.money.api.Money
import me.bookk.feature.business.domain.api.entity.Business
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
    createdAt = Instant.fromEpochMilliseconds(0),
    permissions = stubBusinessPermissions()
)

internal fun stubBusiness(id: Uuid = Uuid.random(), ownerId: Uuid? = Uuid.random()) = Business(
    id = id,
    name = "Test Business",
    description = "",
    address = "",
    location = null,
    currency = Currency("USD"),
    timeZone = TimeZone.UTC,
    socials = emptyMap(),
    schedule = WorkingSchedule(),
    permissions = stubBusinessPermissions(),
    ownerId = ownerId
)

internal fun stubEmployeeInvitation(businessId: Uuid = Uuid.random()) = EmployeeInvitation(
    id = Uuid.random(),
    businessId = businessId,
    invitedBy = Uuid.random(),
    code = "ABCD1234",
    status = EmployeeInvitationStatus.PENDING,
    createdAt = LocalDateTime(2024, 1, 1, 0, 0)
)

internal fun stubBusinessPermissions() = BusinessPermissions(
    business = ResourcePermission(),
    employees = ResourcePermission(),
    clients = ResourcePermission(),
    services = ResourcePermission(),
    appointments = ResourcePermission()
)

internal fun stubService(businessId: Uuid = Uuid.random()) = Service(
    id = Uuid.random(),
    businessId = businessId,
    group = ServiceGroup(
        id = Uuid.random(),
        businessId = businessId,
        name = "Hair",
        createdAt = Instant.fromEpochMilliseconds(0)
    ),
    name = "Haircut",
    duration = 30.minutes,
    price = Money(value = 2000, currencyType = Money.SupportedCurrency.EUR),
    isAvailable = true,
    createdAt = Instant.fromEpochMilliseconds(0)
)
