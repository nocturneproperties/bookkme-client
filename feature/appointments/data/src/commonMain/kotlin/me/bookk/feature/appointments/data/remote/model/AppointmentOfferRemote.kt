package me.bookk.feature.appointments.data.remote.model

import kotlinx.serialization.Serializable
import kotlinx.serialization.protobuf.ProtoNumber

@Serializable
data class AppointmentOfferRemote(
    @ProtoNumber(1) val request: AppointmentRequestRemote,
    @ProtoNumber(2) val offerToken: String
)
