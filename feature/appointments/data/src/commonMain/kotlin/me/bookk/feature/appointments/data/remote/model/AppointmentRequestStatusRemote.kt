package me.bookk.feature.appointments.data.remote.model

import kotlinx.serialization.Serializable
import me.bookk.feature.appointments.domain.api.entity.AppointmentRequestStatus

@Serializable
enum class AppointmentRequestStatusRemote {
    PENDING,
    APPROVED,
    DECLINED;

    fun toDomain() = when (this) {
        PENDING -> AppointmentRequestStatus.PENDING
        APPROVED -> AppointmentRequestStatus.APPROVED
        DECLINED -> AppointmentRequestStatus.DECLINED
    }
}