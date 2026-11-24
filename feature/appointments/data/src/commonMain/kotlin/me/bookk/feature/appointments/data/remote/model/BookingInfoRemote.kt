package me.bookk.feature.appointments.data.remote.model

import kotlinx.serialization.Serializable
import kotlinx.serialization.protobuf.ProtoNumber
import me.bookk.feature.appointments.domain.api.entity.BookingInfo
import kotlin.uuid.Uuid

@Serializable
data class BookingInfoRemote(
    @ProtoNumber(1) val businessId: Uuid,
    @ProtoNumber(2) val appointmentNote: String = "",
) {
    fun toDomain(): BookingInfo {
        return BookingInfo(businessId = businessId, appointmentNote = appointmentNote)
    }
}
