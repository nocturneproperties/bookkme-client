package me.bookk.feature.appointments.presentation

import me.bookk.feature.appointments.presentation.screen.requestlist.AppointmentListState

interface AppointmentsStateFactory {
    fun createAppointmentListState(): AppointmentListState
}
