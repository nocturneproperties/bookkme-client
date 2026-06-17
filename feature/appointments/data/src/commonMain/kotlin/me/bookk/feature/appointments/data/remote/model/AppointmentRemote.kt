package me.bookk.feature.appointments.data.remote.model

import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlinx.serialization.Serializable
import me.bookk.feature.appointments.domain.api.entity.Appointment
import kotlin.time.Instant
import kotlin.uuid.Uuid

@Serializable
data class AppointmentRemote(
    val id: Uuid,
    val userId: Uuid,
    val businessId: Uuid,
    val client: ClientSnapshotRemote,
    val services: List<ServiceSnapshotRemote>,
    val status: AppointmentStatusRemote,
    val date: Instant,
    val note: String,
    val cancellationReason: String
) {
    fun toDomain() = Appointment(
        id = id,
        userId = userId,
        businessId = businessId,
        client = client.toDomain(),
        services = services.map { it.toDomain() },
        status = status.toDomain(),
        date = date.toLocalDateTime(TimeZone.currentSystemDefault()),
        note = note,
        cancellationReason = cancellationReason
    )
}