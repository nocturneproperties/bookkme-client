package me.bookk.feature.appointments.data.mapping

import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime
import library.money.api.Money
import me.bookk.database.entity.AppointmentEntity
import me.bookk.database.entity.AppointmentRequestEntity
import me.bookk.database.entity.AppointmentRequestServiceSnapshotEntity
import me.bookk.database.entity.AppointmentServiceSnapshotEntity
import me.bookk.database.entity.AppointmentSettingsDayOffEntity
import me.bookk.database.entity.AppointmentSettingsDayScheduleEntity
import me.bookk.database.entity.AppointmentSettingsEntity
import me.bookk.database.entity.AppointmentSettingsWorkHourEntity
import me.bookk.database.relation.AppointmentLocal
import me.bookk.database.relation.AppointmentRequestLocal
import me.bookk.database.relation.AppointmentSettingsLocal
import me.bookk.feature.appointments.data.remote.model.AppointmentCancellationRemote
import me.bookk.feature.appointments.data.remote.model.AppointmentRemote
import me.bookk.feature.appointments.data.remote.model.AppointmentRequestRemote
import me.bookk.feature.appointments.data.remote.model.AppointmentRequestStatusRemote
import me.bookk.feature.appointments.data.remote.model.AppointmentSettingsUpdateRemote
import me.bookk.feature.appointments.data.remote.model.AppointmentStatusRemote
import me.bookk.feature.appointments.data.remote.model.ClientSnapshotRemote
import me.bookk.feature.appointments.data.remote.model.EmployeeSnapshotRemote
import me.bookk.feature.appointments.data.remote.model.ServiceSnapshotRemote
import me.bookk.feature.appointments.domain.api.entity.Appointment
import me.bookk.feature.appointments.domain.api.entity.AppointmentCancellation
import me.bookk.feature.appointments.domain.api.entity.AppointmentRequest
import me.bookk.feature.appointments.domain.api.entity.AppointmentRequestStatus
import me.bookk.feature.appointments.domain.api.entity.AppointmentSettings
import me.bookk.feature.appointments.domain.api.entity.AppointmentStatus
import me.bookk.feature.appointments.domain.api.entity.ClientSnapshot
import me.bookk.feature.appointments.domain.api.entity.EmployeeSnapshot
import me.bookk.feature.appointments.domain.api.entity.ServiceSnapshot
import me.bookk.feature.business.domain.api.entity.DayOfWeekSchedule
import me.bookk.feature.business.domain.api.entity.DayOffRange
import me.bookk.feature.business.domain.api.entity.WorkHour
import me.bookk.feature.business.domain.api.entity.WorkingSchedule

internal fun AppointmentRequest.toRemote() = AppointmentRequestRemote(
    id = id,
    userId = userId,
    businessId = businessId,
    employee = employee.toRemote(),
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
    employee = employee.toRemote(),
    client = client.toRemote(),
    services = services.map { it.toRemote() },
    status = status.toRemote(),
    date = date.toInstant(TimeZone.currentSystemDefault()),
    note = note,
    cancellationReason = cancellationReason
)


internal fun AppointmentCancellation.toRemote() = AppointmentCancellationRemote(
    id = id,
    businessId = businessId,
    reason = reason
)

private fun EmployeeSnapshot.toRemote() = EmployeeSnapshotRemote(
    id = id,
    fullName = fullName
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
    AppointmentRequestStatus.CANCELLED -> AppointmentRequestStatusRemote.CANCELLED
}

private fun AppointmentStatus.toRemote() = when (this) {
    AppointmentStatus.SCHEDULED -> AppointmentStatusRemote.SCHEDULED
    AppointmentStatus.COMPLETED -> AppointmentStatusRemote.COMPLETED
    AppointmentStatus.CANCELLED -> AppointmentStatusRemote.CANCELLED
}

internal fun Appointment.toEntity() = AppointmentEntity(
    id = id,
    userId = userId,
    businessId = businessId,
    employeeId = employee.id,
    employeeFullName = employee.fullName,
    date = date.toInstant(TimeZone.currentSystemDefault()),
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
    employee = EmployeeSnapshot(id = entity.employeeId, fullName = entity.employeeFullName),
    date = entity.date.toLocalDateTime(TimeZone.currentSystemDefault()),
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

internal fun AppointmentRequest.toRequestEntity() = AppointmentRequestEntity(
    id = id,
    userId = userId,
    businessId = businessId,
    employeeId = employee.id,
    employeeFullName = employee.fullName,
    status = status.name,
    date = date,
    note = note,
    declineReason = declineReason,
    clientId = client.id,
    clientFullName = client.fullName,
    clientPhone = client.phone,
    clientEmail = client.email
)

internal fun AppointmentRequest.toRequestServiceEntities() = services.map { service ->
    AppointmentRequestServiceSnapshotEntity(
        requestId = id,
        id = service.id,
        name = service.name,
        groupId = service.groupId,
        priceCurrency = service.price.currencyType.code,
        priceValue = service.price.value,
        duration = service.duration
    )
}

internal fun AppointmentRequestLocal.toDomain() = AppointmentRequest(
    id = entity.id,
    userId = entity.userId,
    businessId = entity.businessId,
    employee = EmployeeSnapshot(id = entity.employeeId, fullName = entity.employeeFullName),
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
    status = AppointmentRequestStatus.valueOf(entity.status),
    date = entity.date,
    note = entity.note,
    declineReason = entity.declineReason
)

internal fun AppointmentSettings.toRemote() = AppointmentSettingsUpdateRemote(
    businessId = businessId,
    automaticApproval = automaticApproval,
    inBetweenBreakInMinutes = inBetweenBreakInMinutes,
    appointmentNote = appointmentNote
)

internal fun AppointmentSettings.toEntity() = AppointmentSettingsEntity(
    id = id,
    businessId = businessId,
    timeZone = timeZone.id,
    automaticApproval = automaticApproval,
    inBetweenBreakInMinutes = inBetweenBreakInMinutes,
    appointmentNote = appointmentNote
)

internal fun AppointmentSettings.toDayScheduleEntities() = schedule.days.map { (dayOfWeek, daySchedule) ->
    AppointmentSettingsDayScheduleEntity(
        settingsId = id,
        dayOfWeek = dayOfWeek.name,
        isActive = daySchedule.isActive
    )
}

internal fun AppointmentSettings.toWorkHourEntities() = schedule.days.flatMap { (dayOfWeek, daySchedule) ->
    daySchedule.workingTime.map { workHour ->
        AppointmentSettingsWorkHourEntity(
            settingsId = id,
            dayOfWeek = dayOfWeek.name,
            from = workHour.from.toString(),
            to = workHour.to.toString()
        )
    }
}

internal fun AppointmentSettings.toDayOffEntities() = schedule.dayOffs.map { dayOff ->
    AppointmentSettingsDayOffEntity(
        settingsId = id,
        start = dayOff.start.toString(),
        end = dayOff.end.toString()
    )
}

internal fun AppointmentSettingsLocal.toDomain() = AppointmentSettings(
    id = entity.id,
    businessId = entity.businessId,
    timeZone = TimeZone.of(entity.timeZone),
    schedule = WorkingSchedule(
        days = daySchedules.associate { daySchedule ->
            val dayOfWeek = DayOfWeek.valueOf(daySchedule.dayOfWeek)
            dayOfWeek to DayOfWeekSchedule(
                dayOfWeek = dayOfWeek,
                workingTime = workHours.filter { it.dayOfWeek == daySchedule.dayOfWeek }.map {
                    WorkHour(from = LocalTime.parse(it.from), to = LocalTime.parse(it.to))
                },
                isActive = daySchedule.isActive
            )
        },
        dayOffs = dayOffs.map {
            DayOffRange(start = LocalDate.parse(it.start), end = LocalDate.parse(it.end))
        }
    ),
    automaticApproval = entity.automaticApproval,
    inBetweenBreakInMinutes = entity.inBetweenBreakInMinutes,
    appointmentNote = entity.appointmentNote
)
