package me.bookk.feature.appointments.data.remote.model

import kotlinx.serialization.Serializable
import kotlinx.serialization.protobuf.ProtoNumber
import kotlin.time.Instant
import kotlin.uuid.Uuid

@Serializable
data class AppointmentUpdateRemote(
    @ProtoNumber(1) val id: Uuid,
    @ProtoNumber(8) val date: Instant,
    @ProtoNumber(9) val note: String,
    @ProtoNumber(13) val employeeId: Uuid,
    @ProtoNumber(14) val services: List<RequestedServiceRemote>
)

@Serializable
data class RequestedServiceRemote(
    @ProtoNumber(1) val serviceId: Uuid,
    @ProtoNumber(2) val count: Int
)
