package me.bookk.feature.business.data.remote.model

import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime
import kotlinx.serialization.Serializable
import me.bookk.feature.business.domain.api.entity.DayOfWeekSchedule
import me.bookk.feature.business.domain.api.entity.DayOffRange
import me.bookk.feature.business.domain.api.entity.WorkHour
import me.bookk.feature.business.domain.api.entity.WorkingSchedule

/**
 * Wire shape of the business working schedule.
 *
 * Field order is significant: the API speaks protobuf and none of the remote models declare
 * explicit `@ProtoNumber`s, so numbers are assigned by declaration order.
 */
@Serializable
class ScheduleRemote(
    val days: Map<DayOfWeek, DayOfWeekScheduleRemote>,
    val dayOffs: List<DayOffRangeRemote>
) {
    fun toDomain() = WorkingSchedule(
        days = days.mapValues { it.value.toDomain(it.key) },
        dayOffs = dayOffs.map { it.toDomain() }
    )
}

@Serializable
class DayOfWeekScheduleRemote(
    val workingTime: List<WorkHourRemote>,
    val isActive: Boolean
) {
    fun toDomain(dayOfWeek: DayOfWeek) = DayOfWeekSchedule(
        dayOfWeek = dayOfWeek,
        workingTime = workingTime.map { it.toDomain() },
        isActive = isActive
    )
}

@Serializable
class WorkHourRemote(
    val from: LocalTime,
    val to: LocalTime
) {
    fun toDomain() = WorkHour(from = from, to = to)
}

@Serializable
class DayOffRangeRemote(
    val start: LocalDate,
    val end: LocalDate
) {
    fun toDomain() = DayOffRange(start = start, end = end)
}

fun WorkingSchedule.toRemote() = ScheduleRemote(
    days = days.mapValues { it.value.toRemote() },
    dayOffs = dayOffs.map { it.toRemote() }
)

private fun DayOfWeekSchedule.toRemote() = DayOfWeekScheduleRemote(
    workingTime = workingTime.map { it.toRemote() },
    isActive = isActive
)

private fun WorkHour.toRemote() = WorkHourRemote(from = from, to = to)

private fun DayOffRange.toRemote() = DayOffRangeRemote(start = start, end = end)
