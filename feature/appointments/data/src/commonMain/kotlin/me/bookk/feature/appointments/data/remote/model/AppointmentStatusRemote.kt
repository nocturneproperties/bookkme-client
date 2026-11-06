package me.bookk.feature.appointments.data.remote.model

import kotlinx.serialization.Serializable
import me.bookk.feature.appointments.domain.api.entity.AppointmentStatus

@Serializable
enum class AppointmentStatusRemote {
    SCHEDULED,
    COMPLETED,
    CANCELLED,
    NO_SHOW,
    UNRESOLVED,
    ;

    fun toDomain(): AppointmentStatus {
        return when (this) {
            SCHEDULED -> AppointmentStatus.SCHEDULED
            COMPLETED -> AppointmentStatus.COMPLETED
            CANCELLED -> AppointmentStatus.CANCELLED
            NO_SHOW -> AppointmentStatus.NO_SHOW
            UNRESOLVED -> AppointmentStatus.UNRESOLVED
        }
    }
}
