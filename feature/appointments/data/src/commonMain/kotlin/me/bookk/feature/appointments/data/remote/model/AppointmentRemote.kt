package me.bookk.feature.appointments.data.remote.model

import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlinx.serialization.Serializable
import kotlinx.serialization.protobuf.ProtoNumber
import me.bookk.feature.appointments.domain.api.entity.Appointment
import kotlin.time.Instant
import kotlin.uuid.Uuid

@Serializable
data class AppointmentRemote(
    @ProtoNumber(1) val id: Uuid,
    @ProtoNumber(2) val userId: Uuid,
    @ProtoNumber(3) val businessId: Uuid,
    @ProtoNumber(4) val employee: EmployeeSnapshotRemote,
    @ProtoNumber(5) val client: ClientSnapshotRemote,
    @ProtoNumber(6) val services: List<ServiceSnapshotRemote>,
    @ProtoNumber(7) val status: AppointmentStatusRemote,
    @ProtoNumber(8) val date: Instant,
    @ProtoNumber(9) val note: String,
    @ProtoNumber(10) val cancellationReason: String
) {
    fun toDomain() = Appointment(
        id = id,
        userId = userId,
        businessId = businessId,
        employee = employee.toDomain(),
        client = client.toDomain(),
        services = services.map { it.toDomain() },
        status = status.toDomain(),
        date = date.toLocalDateTime(TimeZone.currentSystemDefault()),
        note = note,
        cancellationReason = cancellationReason
    )
}
