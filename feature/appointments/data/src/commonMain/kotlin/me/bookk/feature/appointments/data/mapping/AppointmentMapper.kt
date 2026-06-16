package me.bookk.feature.appointments.data.mapping

import kotlinx.datetime.LocalDate
import library.money.api.Money
import me.bookk.database.entity.AppointmentEntity
import me.bookk.database.entity.AppointmentServiceSnapshotEntity
import me.bookk.database.relation.AppointmentLocal
import me.bookk.feature.appointments.data.remote.model.AppointmentCancellationRemote
import me.bookk.feature.appointments.data.remote.model.AppointmentRemote
import me.bookk.feature.appointments.data.remote.model.AppointmentRequestRemote
import me.bookk.feature.appointments.data.remote.model.AppointmentRequestStatusRemote
import me.bookk.feature.appointments.data.remote.model.AppointmentStatusRemote
import me.bookk.feature.appointments.data.remote.model.ClientSnapshotRemote
import me.bookk.feature.appointments.data.remote.model.ServiceSnapshotRemote
import me.bookk.feature.appointments.domain.api.entity.Appointment
import me.bookk.feature.appointments.domain.api.entity.AppointmentCancellation
import me.bookk.feature.appointments.domain.api.entity.AppointmentRequest
import me.bookk.feature.appointments.domain.api.entity.AppointmentRequestStatus
import me.bookk.feature.appointments.domain.api.entity.AppointmentStatus
import me.bookk.feature.appointments.domain.api.entity.ClientSnapshot
import me.bookk.feature.appointments.domain.api.entity.ServiceSnapshot

internal fun AppointmentRequest.toRemote() = AppointmentRequestRemote(
    id = id,
    userId = userId,
    businessId = businessId,
    client = client.toRemote(),
    services = services.map { it.toRemote() },
    status = status.toRemote(),
    date = date,
    note = note,
    declineReason = declineReason
)

internal fun Appointment.toRemote() = AppointmentRemote(
    id = id,
    userId = userId,
    businessId = businessId,
    client = client.toRemote(),
    services = services.map { it.toRemote() },
    status = status.toRemote(),
    date = date,
    note = note,
    cancellationReason = cancellationReason
)


internal fun AppointmentCancellation.toRemote() = AppointmentCancellationRemote(
    id = id,
    businessId = businessId,
    reason = reason
)

private fun ClientSnapshot.toRemote() = ClientSnapshotRemote(
    id = id,
    fullName = fullName,
    phone = phone,
    email = email
)

private fun ServiceSnapshot.toRemote() = ServiceSnapshotRemote(
    id = id,
    name = name,
    groupId = groupId,
    price = price,
    duration = duration
)

private fun AppointmentRequestStatus.toRemote() = when (this) {
    AppointmentRequestStatus.PENDING -> AppointmentRequestStatusRemote.PENDING
    AppointmentRequestStatus.APPROVED -> AppointmentRequestStatusRemote.APPROVED
    AppointmentRequestStatus.DECLINED -> AppointmentRequestStatusRemote.DECLINED
}

private fun AppointmentStatus.toRemote() = when (this) {
    AppointmentStatus.SCHEDULED -> AppointmentStatusRemote.SCHEDULED
    AppointmentStatus.COMPLETED -> AppointmentStatusRemote.COMPLETED
    AppointmentStatus.CANCELLED -> AppointmentStatusRemote.CANCELLED
}

internal fun Appointment.toEntity(localDate: LocalDate) = AppointmentEntity(
    id = id,
    userId = userId,
    businessId = businessId,
    date = date,
    localDate = localDate.toString(),
    status = status.name,
    note = note,
    cancellationReason = cancellationReason,
    clientId = client.id,
    clientFullName = client.fullName,
    clientPhone = client.phone,
    clientEmail = client.email
)

internal fun Appointment.toServiceEntities() = services.map { service ->
    AppointmentServiceSnapshotEntity(
        appointmentId = id,
        id = service.id,
        name = service.name,
        groupId = service.groupId,
        priceCurrency = service.price.currencyType.code,
        priceValue = service.price.value,
        duration = service.duration
    )
}

internal fun AppointmentLocal.toDomain() = Appointment(
    id = entity.id,
    userId = entity.userId,
    businessId = entity.businessId,
    date = entity.date,
    client = ClientSnapshot(
        id = entity.clientId,
        fullName = entity.clientFullName,
        phone = entity.clientPhone,
        email = entity.clientEmail
    ),
    services = services.map { service ->
        ServiceSnapshot(
            id = service.id,
            name = service.name,
            groupId = service.groupId,
            price = Money(service.priceValue, Money.SupportedCurrency.fromCode(service.priceCurrency)),
            duration = service.duration
        )
    },
    status = AppointmentStatus.valueOf(entity.status),
    note = entity.note,
    cancellationReason = entity.cancellationReason
)
