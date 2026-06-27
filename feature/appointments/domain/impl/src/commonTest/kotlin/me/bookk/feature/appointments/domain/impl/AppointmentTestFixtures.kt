package me.bookk.feature.appointments.domain.impl

import kotlinx.datetime.LocalDateTime
import library.money.api.Money
import me.bookk.feature.appointments.domain.api.entity.Appointment
import me.bookk.feature.appointments.domain.api.entity.AppointmentRequest
import me.bookk.feature.appointments.domain.api.entity.AppointmentRequestStatus
import me.bookk.feature.appointments.domain.api.entity.AppointmentStatus
import me.bookk.feature.appointments.domain.api.entity.ClientSnapshot
import me.bookk.feature.appointments.domain.api.entity.ServiceSnapshot
import kotlin.time.Duration
import kotlin.time.Instant
import kotlin.uuid.Uuid

internal fun stubServiceSnapshot(groupId: Uuid = Uuid.random()) = ServiceSnapshot(
    id = Uuid.random(),
    name = "Haircut",
    groupId = groupId,
    price = Money(1000L, Money.SupportedCurrency.USD),
    duration = Duration.parse("30m")
)

internal fun stubClientSnapshot() = ClientSnapshot(
    id = Uuid.random(),
    fullName = "Jane Doe",
    phone = "123456789",
    email = "jane@example.com"
)

internal fun stubAppointment(
    id: Uuid = Uuid.random(),
    userId: Uuid = Uuid.random(),
    businessId: Uuid = Uuid.random(),
    note: String = ""
) = Appointment(
    id = id,
    userId = userId,
    businessId = businessId,
    client = stubClientSnapshot(),
    services = listOf(stubServiceSnapshot()),
    status = AppointmentStatus.SCHEDULED,
    date = LocalDateTime(2024, 6, 15, 10, 0),
    note = note,
    cancellationReason = ""
)

internal fun stubAppointmentRequest(
    id: Uuid = Uuid.random(),
    businessId: Uuid = Uuid.random()
) = AppointmentRequest(
    id = id,
    userId = Uuid.random(),
    businessId = businessId,
    client = stubClientSnapshot(),
    services = listOf(stubServiceSnapshot()),
    status = AppointmentRequestStatus.PENDING,
    date = Instant.fromEpochMilliseconds(0),
    note = "",
    declineReason = ""
)
