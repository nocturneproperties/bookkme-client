package me.bookk.feature.appointments.domain.api.entity

import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant
import library.money.api.Money
import me.bookk.core.now
import kotlin.time.Duration
import kotlin.time.Duration.Companion.days
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
    val priceAdjustment: PriceAdjustment? = null,
) {

    val providedServices: List<ServiceSnapshot> by lazy(LazyThreadSafetyMode.NONE) {
        priceAdjustment?.additionalServices?.ifEmpty { null } ?: services
    }

    val bookedPrice: Money by lazy(LazyThreadSafetyMode.NONE) {
        services.map { it.price }.reduce { acc, money -> acc + money }
    }

    val finalPrice: Money by lazy(LazyThreadSafetyMode.NONE) {
        priceAdjustment?.price ?: bookedPrice
    }

    val total: String by lazy(LazyThreadSafetyMode.NONE) {
        bookedPrice.toString()
    }

    fun canBeCompleted(now: LocalDateTime = LocalDateTime.now()): Boolean {
        return status == AppointmentStatus.SCHEDULED && hasStarted(now) && isWithinEditWindow(now)
    }

    fun canBeMarkedNoShow(now: LocalDateTime = LocalDateTime.now()): Boolean {
        val markableStatus =
            status == AppointmentStatus.SCHEDULED || status == AppointmentStatus.COMPLETED
        return markableStatus && hasStarted(now) && isWithinEditWindow(now)
    }

    fun canBeRescheduled(now: LocalDateTime = LocalDateTime.now()): Boolean {
        return status == AppointmentStatus.SCHEDULED && !hasStarted(now)
    }

    fun canBeCancelled(now: LocalDateTime = LocalDateTime.now()): Boolean {
        return status == AppointmentStatus.SCHEDULED && isWithinEditWindow(now)
    }

    private fun hasStarted(now: LocalDateTime): Boolean {
        return date <= now
    }

    private fun isWithinEditWindow(now: LocalDateTime): Boolean {
        val timeZone = TimeZone.currentSystemDefault()
        val duration = services.fold(Duration.ZERO) { total, service -> total + service.duration }
        val deadline = date.toInstant(timeZone) + duration + 7.days
        return now.toInstant(timeZone) <= deadline
    }

    companion object {
        fun stub(
            id: Uuid = Uuid.random(),
            userId: Uuid = Uuid.random(),
            businessId: Uuid = Uuid.random(),
            date: LocalDateTime = LocalDateTime.now(),
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
            cancellationReason = "",
        )
    }
}
