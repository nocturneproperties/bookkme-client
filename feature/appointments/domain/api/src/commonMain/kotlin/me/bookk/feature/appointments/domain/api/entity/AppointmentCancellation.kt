package me.bookk.feature.appointments.domain.api.entity

import kotlin.uuid.Uuid

data class AppointmentCancellation(
    val id: Uuid,
    val businessId: Uuid,
    val reason: String
) {
    companion object {
        fun stub(
            id: Uuid = Uuid.random(),
            businessId: Uuid = Uuid.random(),
        ) = AppointmentCancellation(
            id = id,
            businessId = businessId,
            reason = "Test reason"
        )
    }
}