package me.bookk.feature.appointments.domain.api.entity

import kotlinx.datetime.DayOfWeek

data class WorkingSchedule(
    val days: Map<DayOfWeek, DayOfWeekSchedule>
) {
    constructor() : this(
        days = DayOfWeek.entries.associateWith { DayOfWeekSchedule.default(it) }
    )
}