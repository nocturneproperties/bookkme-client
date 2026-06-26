package me.bookk.feature.appointments.data.remote.model

import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime
import kotlinx.datetime.TimeZone
import kotlinx.serialization.Serializable
import me.bookk.core.data.TimeZoneSerializer
import me.bookk.feature.appointments.domain.api.entity.AppointmentSettings
import me.bookk.feature.appointments.domain.api.entity.DayOfWeekSchedule
import me.bookk.feature.appointments.domain.api.entity.DayOffRange
import me.bookk.feature.appointments.domain.api.entity.WorkHour
import me.bookk.feature.appointments.domain.api.entity.WorkingSchedule
import kotlin.uuid.Uuid

@Serializable
data class AppointmentSettingsRemote(
    val id: Uuid,
    val businessId: Uuid,
    @Serializable(with = TimeZoneSerializer::class)
    val timeZone: TimeZone,
    val schedule: WorkingScheduleRemote,
    val dayOffs: List<DayOffRangeRemote>,
    val automaticApproval: Boolean,
    val inBetweenBreakInMinutes: Int,
    val appointmentNote: String
) {
    fun toDomain() = AppointmentSettings(
        id = id,
        businessId = businessId,
        timeZone = timeZone,
        schedule = schedule.toDomain(),
        dayOffs = dayOffs.map { it.toDomain() },
        automaticApproval = automaticApproval,
        inBetweenBreakInMinutes = inBetweenBreakInMinutes,
        appointmentNote = appointmentNote
    )
}

@Serializable
data class WorkingScheduleRemote(
    val days: Map<DayOfWeek, DayOfWeekScheduleRemote>
) {
    fun toDomain() = WorkingSchedule(days = days.mapValues { it.value.toDomain(it.key) })
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
    val dayOfWeek: DayOfWeek,
    val from: LocalTime,
    val to: LocalTime
) {
    fun toDomain() = WorkHour(
        dayOfWeek = dayOfWeek,
        from = from,
        to = to
    )
}

@Serializable
data class DayOffRangeRemote(
    val start: LocalDate,
    val end: LocalDate
) {
    fun toDomain() = DayOffRange(
        start = start,
        end = end
    )
}