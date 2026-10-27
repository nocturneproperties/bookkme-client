package me.bookk.feature.appointments.data.remote.model

import kotlinx.serialization.Serializable
import kotlinx.serialization.protobuf.ProtoNumber
import kotlin.time.Instant
import kotlin.uuid.Uuid

@Serializable
data class AppointmentRequestDraftRemote(
    @ProtoNumber(1) val businessId: Uuid,
    @ProtoNumber(2) val employeeId: Uuid,
    @ProtoNumber(3) val services: List<RequestedServiceRemote>,
    @ProtoNumber(4) val date: Instant,
    @ProtoNumber(5) val note: String,
    @ProtoNumber(6) val offerToken: String
)
