package me.bookk.feature.business.domain.impl

import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime
import kotlinx.datetime.TimeZone
import library.money.api.Currency
import me.bookk.feature.business.domain.api.entity.Business
import me.bookk.feature.business.domain.api.entity.DayOfWeekSchedule
import me.bookk.feature.business.domain.api.entity.DayOffRange
import me.bookk.feature.business.domain.api.entity.WorkHour
import me.bookk.feature.business.domain.api.entity.WorkingSchedule
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
    schedule: WorkingSchedule = stubWorkingSchedule()
) = Business(
    id = id,
    name = "Test Business",
    description = "",
    address = "",
    location = null,
    currency = Currency("USD"),
    timeZone = TimeZone.UTC,
    socials = emptyMap(),
    schedule = schedule
)
