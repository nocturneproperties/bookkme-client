package me.bookk.feature.appointments.data.remote.model

import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime
import kotlinx.datetime.TimeZone
import kotlinx.serialization.Serializable
import kotlinx.serialization.protobuf.ProtoNumber
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
 * Field numbers are pinned explicitly via `@ProtoNumber` since the API speaks protobuf; a new
 * field must get the next unused number and an existing number must never be reassigned.
 */
@Serializable
data class AppointmentSettingsRemote(
    @ProtoNumber(1) val id: Uuid,
    @ProtoNumber(2) val businessId: Uuid,
    @ProtoNumber(3)
    @Serializable(with = TimeZoneSerializer::class)
    val timeZone: TimeZone,
    @ProtoNumber(4) val schedule: WorkingScheduleRemote,
    @ProtoNumber(5) val automaticApproval: Boolean,
    @ProtoNumber(6) val inBetweenBreakInMinutes: Int,
    @ProtoNumber(7) val appointmentNote: String
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
    @ProtoNumber(1) val businessId: Uuid,
    @ProtoNumber(2) val automaticApproval: Boolean,
    @ProtoNumber(3) val inBetweenBreakInMinutes: Int,
    @ProtoNumber(4) val appointmentNote: String
)

@Serializable
data class WorkingScheduleRemote(
    @ProtoNumber(1) val days: Map<DayOfWeek, DayOfWeekScheduleRemote>,
    @ProtoNumber(2) val dayOffs: List<DayOffRangeRemote>
) {
    fun toDomain() = WorkingSchedule(
        days = days.mapValues { it.value.toDomain(it.key) },
        dayOffs = dayOffs.map { it.toDomain() }
    )
}

@Serializable
data class DayOfWeekScheduleRemote(
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
data class WorkHourRemote(
    @ProtoNumber(1) val from: LocalTime,
    @ProtoNumber(2) val to: LocalTime
) {
    fun toDomain() = WorkHour(from = from, to = to)
}

@Serializable
data class DayOffRangeRemote(
    @ProtoNumber(1) val start: LocalDate,
    @ProtoNumber(2) val end: LocalDate
) {
    fun toDomain() = DayOffRange(start = start, end = end)
}
