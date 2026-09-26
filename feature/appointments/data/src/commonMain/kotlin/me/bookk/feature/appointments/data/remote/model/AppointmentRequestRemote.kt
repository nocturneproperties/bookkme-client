package me.bookk.feature.appointments.data.remote.model

import kotlinx.serialization.Serializable
import kotlinx.serialization.protobuf.ProtoNumber
import me.bookk.feature.appointments.domain.api.entity.AppointmentRequest
import kotlin.time.Instant
import kotlin.uuid.Uuid

@Serializable
data class AppointmentRequestRemote(
    @ProtoNumber(1) val id: Uuid,
    @ProtoNumber(2) val userId: Uuid,
    @ProtoNumber(3) val businessId: Uuid,
    @ProtoNumber(4) val employee: EmployeeSnapshotRemote,
    @ProtoNumber(5) val client: ClientSnapshotRemote,
    @ProtoNumber(6) val services: List<ServiceSnapshotRemote>,
    @ProtoNumber(7) val status: AppointmentRequestStatusRemote,
    @ProtoNumber(8) val date: Instant,
    @ProtoNumber(9) val note: String,
    @ProtoNumber(10) val declineReason: String,
) {
    fun toDomain() = AppointmentRequest(
        id = id,
        userId = userId,
        businessId = businessId,
        employee = employee.toDomain(),
        client = client.toDomain(),
        services = services.map { it.toDomain() },
        status = status.toDomain(),
        date = date,
        note = note,
        declineReason = declineReason
    )
}
