package me.bookk.feature.appointments.domain.api.entity

import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import me.bookk.feature.business.domain.api.entity.WorkingSchedule
import kotlin.time.Instant
import kotlin.uuid.Uuid

data class AppointmentSettings(
    val id: Uuid,
    val businessId: Uuid,
    val timeZone: TimeZone,
    val schedule: WorkingSchedule,
    val automaticApproval: Boolean,
    val inBetweenBreakInMinutes: Int,
    val appointmentNote: String
) {
    companion object {
        fun stub(businessId: Uuid = Uuid.random()) = AppointmentSettings(businessId = businessId)
    }

    constructor(businessId: Uuid) : this(
        id = Uuid.random(),
        businessId = businessId,
        timeZone = TimeZone.of("UTC"),
        schedule = WorkingSchedule(),
        automaticApproval = false,
        inBetweenBreakInMinutes = 10,
        appointmentNote = ""
    )

    fun isInWorkday(date: Instant): Boolean {
        val localDate = date.toLocalDateTime(timeZone)
        if (!schedule.days.getValue(localDate.dayOfWeek).isActive) return false
        return schedule.dayOffs.none { localDate.date in it.start..it.end }
    }

    fun isInWorktime(date: Instant): Boolean {
        val localDateTime = date.toLocalDateTime(timeZone)
        val dayOfWeek = localDateTime.dayOfWeek
        val schedule = schedule.days.getValue(dayOfWeek)
        return schedule.workingTime.any { time ->
            localDateTime.time in time.from..time.to
        }
    }
}
