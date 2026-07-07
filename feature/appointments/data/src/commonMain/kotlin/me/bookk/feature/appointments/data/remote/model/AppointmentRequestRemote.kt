package me.bookk.feature.appointments.data.remote.model

import kotlinx.serialization.Serializable
import me.bookk.feature.appointments.domain.api.entity.AppointmentRequest
import kotlin.time.Instant
import kotlin.uuid.Uuid

@Serializable
data class AppointmentRequestRemote(
    val id: Uuid,
    val userId: Uuid,
    val businessId: Uuid,
    val employee: EmployeeSnapshotRemote,
    val client: ClientSnapshotRemote,
    val services: List<ServiceSnapshotRemote>,
    val status: AppointmentRequestStatusRemote,
    val date: Instant,
    val note: String,
    val declineReason: String,
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