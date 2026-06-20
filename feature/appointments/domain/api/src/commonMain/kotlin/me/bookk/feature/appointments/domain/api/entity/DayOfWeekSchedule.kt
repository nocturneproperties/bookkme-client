package me.bookk.feature.appointments.domain.api.entity

import kotlinx.datetime.DayOfWeek

data class DayOfWeekSchedule(
    val workingTime: List<WorkHour>,
    val isActive: Boolean
) {
    companion object {
        fun default(dayOfWeek: DayOfWeek): DayOfWeekSchedule {
            return DayOfWeekSchedule(
                workingTime = listOf(dayOfWeek.nineToFive()),
                isActive = dayOfWeek < DayOfWeek.SATURDAY
            )
        }
    }
}