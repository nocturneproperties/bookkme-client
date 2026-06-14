package me.bookk.feature.appointments.presentation

import me.bookk.feature.appointments.presentation.screen.create.AndroidAppointmentCreateState
import me.bookk.feature.appointments.presentation.screen.create.AppointmentCreateState
import me.bookk.feature.appointments.presentation.screen.requestlist.AndroidAppointmentListState
import me.bookk.feature.appointments.presentation.screen.requestlist.AppointmentListState

class AndroidAppointmentsStateFactory : AppointmentsStateFactory {
    override fun createAppointmentListState(): AppointmentListState {
        return AndroidAppointmentListState()
    }

    override fun createAppointmentCreateState(): AppointmentCreateState {
        return AndroidAppointmentCreateState()
    }
}
