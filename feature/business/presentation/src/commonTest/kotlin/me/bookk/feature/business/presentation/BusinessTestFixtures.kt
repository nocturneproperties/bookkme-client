package me.bookk.feature.business.presentation

import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.TimeZone
import library.money.api.Currency
import me.bookk.feature.business.domain.api.entity.Business
import me.bookk.feature.business.domain.api.entity.BusinessPermissions
import me.bookk.feature.business.domain.api.entity.DayOfWeekSchedule
import me.bookk.feature.business.domain.api.entity.ResourcePermission
import me.bookk.feature.business.domain.api.entity.WorkingSchedule
import kotlin.uuid.Uuid

internal fun stubWorkingSchedule() = WorkingSchedule(
    days = DayOfWeek.entries.associateWith { DayOfWeekSchedule.default(it) },
    dayOffs = emptyList()
)

internal fun stubBusiness(
    id: Uuid = Uuid.random(),
    name: String = "Test Business",
    schedule: WorkingSchedule = stubWorkingSchedule(),
    socials: Map<Business.SocialKind, Business.Social> = emptyMap()
) = Business(
    id = id,
    name = name,
    description = "",
    address = "",
    location = null,
    currency = Currency("USD"),
    timeZone = TimeZone.UTC,
    socials = socials,
    schedule = schedule,
    permissions = BusinessPermissions(
        business = ResourcePermission(),
        employees = ResourcePermission(),
        clients = ResourcePermission(),
        services = ResourcePermission(),
        appointments = ResourcePermission()
    )
)
