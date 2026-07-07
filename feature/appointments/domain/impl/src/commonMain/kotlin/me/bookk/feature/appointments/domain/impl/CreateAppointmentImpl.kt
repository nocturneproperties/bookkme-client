package me.bookk.feature.appointments.domain.impl

import me.bookk.core.domain.entity.onBusinessError
import me.bookk.feature.appointments.domain.api.CreateAppointment
import me.bookk.feature.appointments.domain.api.entity.Appointment
import me.bookk.feature.appointments.domain.api.entity.AppointmentDraft
import me.bookk.feature.appointments.domain.api.entity.AppointmentErrorCodes
import me.bookk.feature.appointments.domain.api.entity.AppointmentEvent
import me.bookk.feature.appointments.domain.api.entity.AppointmentStatus
import me.bookk.feature.appointments.domain.api.entity.EmployeeSnapshot
import me.bookk.feature.appointments.domain.api.entity.appointmentEvents
import me.bookk.feature.appointments.domain.datasource.AppointmentDataSource
import me.bookk.feature.authorization.domain.api.UserProfileCRUD
import kotlin.uuid.Uuid

internal class CreateAppointmentImpl(
    private val appointmentDataSource: AppointmentDataSource,
    private val userProfileCRUD: UserProfileCRUD
) : CreateAppointment {

    override suspend fun invoke(draft: AppointmentDraft): Appointment {
        val profile = userProfileCRUD.get()
        val appointment = Appointment(
            id = Uuid.random(),
            userId = profile.id,
            businessId = draft.businessId,
            employee = EmployeeSnapshot(
                id = profile.id,
                fullName = profile.fullName
            ),
            client = draft.client,
            services = draft.services,
            status = AppointmentStatus.SCHEDULED,
            date = draft.date,
            note = draft.note,
            cancellationReason = ""
        )
        return runCatching {
            appointmentDataSource.createAppointment(appointment).also {
                appointmentEvents.emit(AppointmentEvent.Created(it))
            }
        }.onBusinessError {
            when (it.errorCode) {
                AppointmentErrorCodes.APPOINTMENT_EXISTS -> throw CreateAppointment.Error.AppointmentOverlap()
                AppointmentErrorCodes.TIME_NOT_ALLOWED -> throw CreateAppointment.Error.TimeIsNotAllowed()
                AppointmentErrorCodes.DATE_NOT_ALLOWED -> throw CreateAppointment.Error.DateIsNotAllowed()
            }
        }.getOrThrow()
    }
}
