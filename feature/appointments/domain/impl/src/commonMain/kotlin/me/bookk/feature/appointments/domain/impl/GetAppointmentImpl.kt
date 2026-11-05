package me.bookk.feature.appointments.domain.impl

import me.bookk.feature.appointments.domain.api.GetAppointment
import me.bookk.feature.appointments.domain.api.entity.Appointment
import me.bookk.feature.appointments.domain.datasource.AppointmentDataSource
import kotlin.uuid.Uuid
import me.bookk.core.domain.entity.Error as DomainError

internal class GetAppointmentImpl(private val appointmentDataSource: AppointmentDataSource) :
    GetAppointment {
    override suspend fun invoke(id: Uuid): Appointment {
        return appointmentDataSource.getAppointment(id)
    }

    override suspend fun refresh(id: Uuid): Appointment {
        val appointment = runCatching {
            appointmentDataSource.getAppointmentFromRemote(id)
        }.getOrElse {
            if (it is DomainError.NotFound) {
                throw GetAppointment.Error.NotFound()
            }
            throw it
        }
        appointmentDataSource.saveAppointmentInDB(appointment)
        return appointment
    }
}
