package me.bookk.feature.business.domain.api.entity

import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime

data class WorkingSchedule(
    val days: Map<DayOfWeek, DayOfWeekSchedule>,
    val dayOffs: List<DayOffRange>
) {

    val monday = days[DayOfWeek.MONDAY] ?: DayOfWeekSchedule.disabled(DayOfWeek.MONDAY)
    val tuesday = days[DayOfWeek.TUESDAY] ?: DayOfWeekSchedule.disabled(DayOfWeek.TUESDAY)
    val wednesday = days[DayOfWeek.WEDNESDAY] ?: DayOfWeekSchedule.disabled(DayOfWeek.WEDNESDAY)
    val thursday = days[DayOfWeek.THURSDAY] ?: DayOfWeekSchedule.disabled(DayOfWeek.THURSDAY)
    val friday = days[DayOfWeek.FRIDAY] ?: DayOfWeekSchedule.disabled(DayOfWeek.FRIDAY)
    val saturday = days[DayOfWeek.SATURDAY] ?: DayOfWeekSchedule.disabled(DayOfWeek.SATURDAY)
    val sunday = days[DayOfWeek.SUNDAY] ?: DayOfWeekSchedule.disabled(DayOfWeek.SUNDAY)

    constructor() : this(
        days = DayOfWeek.entries.associateWith { DayOfWeekSchedule.default(it) },
        dayOffs = listOf()
    )
}

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

data class WorkHour(
    val from: LocalTime,
    val to: LocalTime
)

data class DayOffRange(
    val start: LocalDate,
    val end: LocalDate
)

fun DayOfWeek.nineToFive() = WorkHour(
    from = LocalTime(9, 0),
    to = LocalTime(17, 0)
)
