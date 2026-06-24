package me.bookk.feature.appointments.domain.api.entity

import kotlinx.datetime.DayOfWeek

data class WorkingSchedule(
    val days: Map<DayOfWeek, DayOfWeekSchedule>
) {

    val monday = days[DayOfWeek.MONDAY] ?: DayOfWeekSchedule.disabled(DayOfWeek.MONDAY)
    val tuesday = days[DayOfWeek.TUESDAY] ?: DayOfWeekSchedule.disabled(DayOfWeek.TUESDAY)
    val wednesday = days[DayOfWeek.WEDNESDAY] ?: DayOfWeekSchedule.disabled(DayOfWeek.WEDNESDAY)
    val thursday = days[DayOfWeek.THURSDAY] ?: DayOfWeekSchedule.disabled(DayOfWeek.THURSDAY)
    val friday = days[DayOfWeek.FRIDAY] ?: DayOfWeekSchedule.disabled(DayOfWeek.FRIDAY)
    val saturday = days[DayOfWeek.SATURDAY] ?: DayOfWeekSchedule.disabled(DayOfWeek.SATURDAY)
    val sunday = days[DayOfWeek.SUNDAY] ?: DayOfWeekSchedule.disabled(DayOfWeek.SUNDAY)

    constructor() : this(
        days = DayOfWeek.entries.associateWith { DayOfWeekSchedule.default(it) }
    )
}