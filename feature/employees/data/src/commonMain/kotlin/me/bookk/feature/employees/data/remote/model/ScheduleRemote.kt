package me.bookk.feature.employees.data.remote.model

import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime
import kotlinx.serialization.Serializable
import kotlinx.serialization.protobuf.ProtoNumber
import me.bookk.feature.business.domain.api.entity.DayOfWeekSchedule
import me.bookk.feature.business.domain.api.entity.DayOffRange
import me.bookk.feature.business.domain.api.entity.WorkHour
import me.bookk.feature.business.domain.api.entity.WorkingSchedule

@Serializable
internal class ScheduleRemote(
    @ProtoNumber(1) val days: Map<DayOfWeek, DayOfWeekScheduleRemote>,
    @ProtoNumber(2) val dayOffs: List<DayOffRangeRemote>
) {
    fun toDomain() = WorkingSchedule(
        days = days.mapValues { it.value.toDomain(it.key) },
        dayOffs = dayOffs.map { it.toDomain() }
    )

    companion object {
        fun fromDomain(schedule: WorkingSchedule) = ScheduleRemote(
            days = schedule.days.mapValues { it.value.toRemote() },
            dayOffs = schedule.dayOffs.map { it.toRemote() }
        )
    }
}

@Serializable
internal class DayOfWeekScheduleRemote(
    @ProtoNumber(1) val workingTime: List<WorkHourRemote>,
    @ProtoNumber(2) val isActive: Boolean
) {
    fun toDomain(dayOfWeek: DayOfWeek) = DayOfWeekSchedule(
        dayOfWeek = dayOfWeek,
        workingTime = workingTime.map { it.toDomain() },
        isActive = isActive
    )
}

@Serializable
internal class WorkHourRemote(
    @ProtoNumber(1) val from: LocalTime,
    @ProtoNumber(2) val to: LocalTime
) {
    fun toDomain() = WorkHour(from = from, to = to)
}

@Serializable
internal class DayOffRangeRemote(
    @ProtoNumber(1) val start: LocalDate,
    @ProtoNumber(2) val end: LocalDate
) {
    fun toDomain() = DayOffRange(start = start, end = end)
}

private fun DayOfWeekSchedule.toRemote() = DayOfWeekScheduleRemote(
    workingTime = workingTime.map { it.toRemote() },
    isActive = isActive
)

private fun WorkHour.toRemote() = WorkHourRemote(from = from, to = to)

private fun DayOffRange.toRemote() = DayOffRangeRemote(start = start, end = end)
