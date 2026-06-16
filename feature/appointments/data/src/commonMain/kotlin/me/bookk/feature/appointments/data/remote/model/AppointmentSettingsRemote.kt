package me.bookk.feature.appointments.data.remote.model

import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime
import kotlinx.datetime.TimeZone
import kotlinx.serialization.Serializable
import me.bookk.feature.appointments.domain.api.entity.AppointmentSettings
import me.bookk.feature.appointments.domain.api.entity.WorkHour
import kotlin.uuid.Uuid

@Serializable
data class AppointmentSettingsRemote(
    val id: Uuid,
    val businessId: Uuid,
    val timeZone: TimeZone,
    val workingDays: List<DayOfWeek>,
    val workingHours: List<WorkHourRemote>,
    val dayOffs: List<LocalDate>,
    val automaticApproval: Boolean,
    val inBetweenBreakInMinutes: Int,
    val appointmentNote: String
) {
    fun toDomain() = AppointmentSettings(
        id = id,
        businessId = businessId,
        timeZone = timeZone,
        workingDays = workingDays,
        workingHours = workingHours.map { it.toDomain() },
        dayOffs = dayOffs,
        automaticApproval = automaticApproval,
        inBetweenBreakInMinutes = inBetweenBreakInMinutes,
        appointmentNote = appointmentNote
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