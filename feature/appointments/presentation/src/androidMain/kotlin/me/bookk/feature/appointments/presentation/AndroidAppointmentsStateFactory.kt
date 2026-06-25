package me.bookk.feature.appointments.presentation

import me.bookk.feature.appointments.presentation.screen.create.AndroidAppointmentCreateState
import me.bookk.feature.appointments.presentation.screen.create.AppointmentCreateState
import me.bookk.feature.appointments.presentation.screen.details.AndroidAppointmentDetailsState
import me.bookk.feature.appointments.presentation.screen.details.AppointmentDetailsState
import me.bookk.feature.appointments.presentation.screen.history.AndroidAppointmentHistoryState
import me.bookk.feature.appointments.presentation.screen.history.AppointmentHistoryState
import me.bookk.feature.appointments.presentation.screen.list.AndroidAppointmentListState
import me.bookk.feature.appointments.presentation.screen.list.AppointmentListState
import me.bookk.feature.appointments.presentation.screen.request.AndroidAppointmentRequestState
import me.bookk.feature.appointments.presentation.screen.request.AppointmentRequestState
import me.bookk.feature.appointments.presentation.screen.settings.AndroidAppointmentSettingsState
import me.bookk.feature.appointments.presentation.screen.settings.AppointmentSettingsState

class AndroidAppointmentsStateFactory : AppointmentsStateFactory {
    override fun createAppointmentListState(): AppointmentListState {
        return AndroidAppointmentListState()
    }

    override fun createAppointmentCreateState(): AppointmentCreateState {
        return AndroidAppointmentCreateState()
    }

    override fun createAppointmentDetailsState(): AppointmentDetailsState {
        return AndroidAppointmentDetailsState()
    }

    override fun createAppointmentSettingsState(): AppointmentSettingsState {
        return AndroidAppointmentSettingsState()
    }

    override fun createAppointmentHistoryState(): AppointmentHistoryState {
        return AndroidAppointmentHistoryState()
    }

    override fun createAppointmentRequestState(): AppointmentRequestState {
        return AndroidAppointmentRequestState()
    }
}
