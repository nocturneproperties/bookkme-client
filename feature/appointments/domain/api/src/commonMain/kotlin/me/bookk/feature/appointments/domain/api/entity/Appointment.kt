package me.bookk.feature.appointments.domain.api.entity

import kotlinx.datetime.LocalDateTime
import me.bookk.core.now
import kotlin.uuid.Uuid

data class Appointment(
    val id: Uuid,
    val userId: Uuid,
    val businessId: Uuid,
    val employee: EmployeeSnapshot,
    val client: ClientSnapshot,
    val services: List<ServiceSnapshot>,
    val status: AppointmentStatus,
    val date: LocalDateTime,
    val note: String,
    val cancellationReason: String,
    val completedBy: AppointmentCompletedBy? = null,
    val priceAdjustment: PriceAdjustment? = null
) {

    val total: String by lazy(LazyThreadSafetyMode.NONE) {
        services.map { it.price }.reduce { acc, money -> acc + money }.toString()
    }

    fun canBeCompleted(now: LocalDateTime = LocalDateTime.now()): Boolean {
        return status == AppointmentStatus.SCHEDULED && hasStarted(now)
    }

    fun canBeMarkedNoShow(now: LocalDateTime = LocalDateTime.now()): Boolean {
        val markableStatus = status == AppointmentStatus.SCHEDULED || status == AppointmentStatus.COMPLETED
        return markableStatus && hasStarted(now)
    }

    private fun hasStarted(now: LocalDateTime): Boolean {
        return date <= now
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
            employee = EmployeeSnapshot.stub(),
            client = ClientSnapshot.stub(),
            services = listOf(ServiceSnapshot.stub()),
            status = AppointmentStatus.SCHEDULED,
            date = date,
            note = "Note",
            cancellationReason = ""
        )
    }
}