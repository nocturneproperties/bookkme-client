package me.bookk.feature.appointments.domain.api.entity

import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant
import library.money.api.Money
import me.bookk.core.now
import kotlin.time.Duration
import kotlin.time.Duration.Companion.days
import kotlin.time.Instant
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

    fun canBeCompleted(now: Instant, timeZone: TimeZone): Boolean {
        return status == AppointmentStatus.SCHEDULED &&
            hasStarted(now, timeZone) &&
            isWithinEditWindow(now, timeZone)
    }

    fun canBeMarkedNoShow(now: Instant, timeZone: TimeZone): Boolean {
        val markableStatus =
            status == AppointmentStatus.SCHEDULED || status == AppointmentStatus.COMPLETED
        return markableStatus && hasStarted(now, timeZone) && isWithinEditWindow(now, timeZone)
    }

    fun canBeRescheduled(now: Instant, timeZone: TimeZone): Boolean {
        return status == AppointmentStatus.SCHEDULED && !hasStarted(now, timeZone)
    }

    fun canBeCancelled(now: Instant, timeZone: TimeZone): Boolean {
        return status == AppointmentStatus.SCHEDULED && isWithinEditWindow(now, timeZone)
    }

    private fun hasStarted(now: Instant, timeZone: TimeZone): Boolean {
        return date.toInstant(timeZone) <= now
    }

    private fun isWithinEditWindow(now: Instant, timeZone: TimeZone): Boolean {
        val duration = services.fold(Duration.ZERO) { total, service -> total + service.duration }
        return now <= date.toInstant(timeZone) + duration + EDIT_WINDOW
    }

    companion object {
        val EDIT_WINDOW: Duration = 7.days

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
