package me.bookk.feature.appointments.data.remote.model

import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime
import kotlinx.datetime.TimeZone
import kotlinx.serialization.Serializable
import me.bookk.core.data.TimeZoneSerializer
import me.bookk.feature.appointments.domain.api.entity.AppointmentSettings
import me.bookk.feature.business.domain.api.entity.DayOfWeekSchedule
import me.bookk.feature.business.domain.api.entity.DayOffRange
import me.bookk.feature.business.domain.api.entity.WorkHour
import me.bookk.feature.business.domain.api.entity.WorkingSchedule
import kotlin.uuid.Uuid

/**
 * Read-only view of the appointment settings.
 *
 * The working schedule and day offs are owned by the business service and only replicated here,
 * so they are never sent back on update — see [AppointmentSettingsUpdateRemote].
 *
 * Field order is significant: the API speaks protobuf and none of the remote models declare
 * explicit `@ProtoNumber`s, so field numbers are assigned by declaration order.
 */
@Serializable
data class AppointmentSettingsRemote(
    val id: Uuid,
    val businessId: Uuid,
    @Serializable(with = TimeZoneSerializer::class)
    val timeZone: TimeZone,
    val schedule: WorkingScheduleRemote,
    val automaticApproval: Boolean,
    val inBetweenBreakInMinutes: Int,
    val appointmentNote: String
) {
    fun toDomain() = AppointmentSettings(
        id = id,
        businessId = businessId,
        timeZone = timeZone,
        schedule = schedule.toDomain(),
        automaticApproval = automaticApproval,
        inBetweenBreakInMinutes = inBetweenBreakInMinutes,
        appointmentNote = appointmentNote
    )
}

@Serializable
data class AppointmentSettingsUpdateRemote(
    val businessId: Uuid,
    val automaticApproval: Boolean,
    val inBetweenBreakInMinutes: Int,
    val appointmentNote: String
)

@Serializable
data class WorkingScheduleRemote(
    val days: Map<DayOfWeek, DayOfWeekScheduleRemote>,
    val dayOffs: List<DayOffRangeRemote>
) {
    fun toDomain() = WorkingSchedule(
        days = days.mapValues { it.value.toDomain(it.key) },
        dayOffs = dayOffs.map { it.toDomain() }
    )
}

@Serializable
data class DayOfWeekScheduleRemote(
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
data class WorkHourRemote(
    val from: LocalTime,
    val to: LocalTime
) {
    fun toDomain() = WorkHour(from = from, to = to)
}

@Serializable
data class DayOffRangeRemote(
    val start: LocalDate,
    val end: LocalDate
) {
    fun toDomain() = DayOffRange(start = start, end = end)
}
