package me.bookk.feature.appointments.domain.api.entity

import kotlin.time.Instant
import kotlin.uuid.Uuid

data class AppointmentRequest(
    val id: Uuid,
    val userId: Uuid,
    val businessId: Uuid,
    val client: ClientSnapshot,
    val services: List<ServiceSnapshot>,
    val status: AppointmentRequestStatus,
    val date: Instant,
    val note: String,
    val declineReason: String,
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
            date: Instant = Instant.fromEpochMilliseconds(0)
        ) = AppointmentRequest(
            id = id,
            userId = userId,
            businessId = businessId,
            client = ClientSnapshot.stub(),
            services = listOf(ServiceSnapshot.stub()),
            status = AppointmentRequestStatus.PENDING,
            date = date,
            note = "Note",
            declineReason = ""
        )
    }
}