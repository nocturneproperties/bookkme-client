package me.bookk.feature.appointments.data.remote.model

import kotlinx.serialization.Serializable
import kotlinx.serialization.protobuf.ProtoNumber
import me.bookk.feature.appointments.domain.api.entity.AppointmentCancellation
import kotlin.uuid.Uuid

@Serializable
data class AppointmentCancellationRemote(
    @ProtoNumber(1) val id: Uuid,
    @ProtoNumber(2) val businessId: Uuid,
    @ProtoNumber(3) val reason: String
) {
    fun toDomain() = AppointmentCancellation(
        id = id,
        businessId = businessId,
        reason = reason
    )
}
