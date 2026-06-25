package me.bookk.feature.appointments.presentation

import me.bookk.feature.appointments.presentation.screen.create.AppointmentCreateState
import me.bookk.feature.appointments.presentation.screen.details.AppointmentDetailsState
import me.bookk.feature.appointments.presentation.screen.history.AppointmentHistoryState
import me.bookk.feature.appointments.presentation.screen.requestlist.AppointmentListState
import me.bookk.feature.appointments.presentation.screen.settings.AppointmentSettingsState

interface AppointmentsStateFactory {
    fun createAppointmentListState(): AppointmentListState
    fun createAppointmentCreateState(): AppointmentCreateState
    fun createAppointmentDetailsState(): AppointmentDetailsState
    fun createAppointmentSettingsState(): AppointmentSettingsState
    fun createAppointmentHistoryState(): AppointmentHistoryState
}
