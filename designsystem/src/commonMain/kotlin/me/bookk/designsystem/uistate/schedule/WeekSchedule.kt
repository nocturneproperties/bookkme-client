package me.bookk.designsystem.uistate.schedule

import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime

data class WeekSchedule(
    val days: List<WeekdaySchedule>,
    val dayOffs: List<DayOffPeriod>
)

data class WeekdaySchedule(
    val dayOfWeek: DayOfWeek,
    val isActive: Boolean,
    val workingHours: List<WorkingHours>
)

data class WorkingHours(
    val from: LocalTime,
    val to: LocalTime
)

data class DayOffPeriod(
    val start: LocalDate,
    val end: LocalDate
)
