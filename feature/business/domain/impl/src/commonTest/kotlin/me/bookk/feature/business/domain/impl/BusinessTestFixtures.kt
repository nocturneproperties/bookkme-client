package me.bookk.feature.business.domain.impl

import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime
import kotlinx.datetime.TimeZone
import library.money.api.Currency
import library.money.api.Money
import me.bookk.feature.business.domain.api.entity.Business
import me.bookk.feature.business.domain.api.entity.BusinessPermissions
import me.bookk.feature.business.domain.api.entity.DayOfWeekSchedule
import me.bookk.feature.business.domain.api.entity.DayOffRange
import me.bookk.feature.business.domain.api.entity.ResourcePermission
import me.bookk.feature.business.domain.api.entity.WorkHour
import me.bookk.feature.business.domain.api.entity.WorkingSchedule
import me.bookk.feature.clients.domain.api.entity.Client
import me.bookk.feature.services.domain.api.group.entity.ServiceGroup
import me.bookk.feature.services.domain.api.service.entity.Service
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Instant
import kotlin.uuid.Uuid

internal fun stubWorkingSchedule(
    days: Map<DayOfWeek, DayOfWeekSchedule> = DayOfWeek.entries.associateWith { DayOfWeekSchedule.default(it) },
    dayOffs: List<DayOffRange> = emptyList()
) = WorkingSchedule(days = days, dayOffs = dayOffs)

internal fun stubDaySchedule(
    dayOfWeek: DayOfWeek = DayOfWeek.MONDAY,
    workingTime: List<WorkHour> = listOf(WorkHour(LocalTime(9, 0), LocalTime(17, 0))),
    isActive: Boolean = true
) = DayOfWeekSchedule(dayOfWeek = dayOfWeek, workingTime = workingTime, isActive = isActive)

internal fun stubDayOff(
    start: LocalDate = LocalDate(2026, 8, 10),
    end: LocalDate = LocalDate(2026, 8, 12)
) = DayOffRange(start = start, end = end)

internal fun stubBusiness(
    id: Uuid = Uuid.random(),
    schedule: WorkingSchedule = stubWorkingSchedule(),
    permissions: BusinessPermissions = stubBusinessPermissions()
) = Business(
    id = id,
    name = "Test Business",
    description = "",
    address = "",
    location = null,
    currency = Currency("USD"),
    timeZone = TimeZone.UTC,
    socials = emptyMap(),
    schedule = schedule,
    permissions = permissions
)

internal fun stubBusinessPermissions() = BusinessPermissions(
    business = ResourcePermission(),
    employees = ResourcePermission(),
    clients = ResourcePermission(),
    services = ResourcePermission(),
    appointments = ResourcePermission()
)

internal fun stubClient(businessId: Uuid = Uuid.random()) = Client.Detached(
    id = Uuid.random(),
    name = "John",
    lastName = "Doe",
    phone = null,
    email = null,
    businessId = businessId
)

internal fun stubServiceGroup(businessId: Uuid = Uuid.random()) = ServiceGroup(
    id = Uuid.random(),
    businessId = businessId,
    name = "Group",
    createdAt = Instant.fromEpochMilliseconds(0)
)

internal fun stubService(businessId: Uuid = Uuid.random()) = Service(
    id = Uuid.random(),
    businessId = businessId,
    group = stubServiceGroup(businessId),
    name = "Haircut",
    duration = 30.minutes,
    price = Money(1000L, Money.SupportedCurrency.USD),
    isAvailable = true,
    createdAt = Instant.fromEpochMilliseconds(0)
)
