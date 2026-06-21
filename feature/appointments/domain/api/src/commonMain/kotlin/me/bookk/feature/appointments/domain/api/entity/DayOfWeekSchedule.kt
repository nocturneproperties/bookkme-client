package me.bookk.feature.appointments.domain.api.entity

import kotlinx.datetime.DayOfWeek

data class DayOfWeekSchedule(
    val dayOfWeek: DayOfWeek,
    val workingTime: List<WorkHour>,
    val isActive: Boolean
) {
    companion object {
        fun default(dayOfWeek: DayOfWeek): DayOfWeekSchedule {
            return DayOfWeekSchedule(
                dayOfWeek = dayOfWeek,
                workingTime = listOf(dayOfWeek.nineToFive()),
                isActive = dayOfWeek < DayOfWeek.SATURDAY
            )
        }

        fun disabled(dayOfWeek: DayOfWeek): DayOfWeekSchedule {
            return DayOfWeekSchedule(
                dayOfWeek = dayOfWeek,
                workingTime = listOf(),
                isActive = false
            )
        }
    }
}