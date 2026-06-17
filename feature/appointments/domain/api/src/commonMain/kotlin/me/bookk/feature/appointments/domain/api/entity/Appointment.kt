package me.bookk.feature.appointments.domain.api.entity

import kotlinx.datetime.LocalDateTime
import me.bookk.core.now
import kotlin.uuid.Uuid

data class Appointment(
    val id: Uuid,
    val userId: Uuid,
    val businessId: Uuid,
    val client: ClientSnapshot,
    val services: List<ServiceSnapshot>,
    val status: AppointmentStatus,
    val date: LocalDateTime,
    val note: String,
    val cancellationReason: String
) {

    val total: String by lazy(LazyThreadSafetyMode.NONE) {
        services
            .fold(services[0].price) { acc, service ->
                acc + service.price
            }
            .toString()
    }

    companion object {
        fun stub(
            id: Uuid = Uuid.random(),
            userId: Uuid = Uuid.random(),
            businessId: Uuid = Uuid.random(),
            date: LocalDateTime = LocalDateTime.now()
        ) = Appointment(
            id = id,
            userId = userId,
            businessId = businessId,
            client = ClientSnapshot.stub(),
            services = listOf(ServiceSnapshot.stub()),
            status = AppointmentStatus.SCHEDULED,
            date = date,
            note = "Note",
            cancellationReason = ""
        )
    }
}