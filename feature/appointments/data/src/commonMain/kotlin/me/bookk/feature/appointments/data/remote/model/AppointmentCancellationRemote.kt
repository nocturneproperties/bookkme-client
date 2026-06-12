package me.bookk.feature.appointments.data.remote.model

import kotlinx.serialization.Serializable
import me.bookk.feature.appointments.domain.api.entity.AppointmentCancellation
import kotlin.uuid.Uuid

@Serializable
data class AppointmentCancellationRemote(
    val id: Uuid,
    val businessId: Uuid,
    val reason: String
) {
    fun toDomain() = AppointmentCancellation(
        id = id,
        businessId = businessId,
        reason = reason
    )
}