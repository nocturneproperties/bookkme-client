package me.bookk.feature.appointments.data.remote.model

import kotlinx.serialization.Serializable
import me.bookk.feature.appointments.domain.api.entity.AppointmentCompletedBy

@Serializable
enum class AppointmentCompletedByRemote {
    SYSTEM,
    USER;

    fun toDomain() = when (this) {
        SYSTEM -> AppointmentCompletedBy.SYSTEM
        USER -> AppointmentCompletedBy.USER
    }
}
