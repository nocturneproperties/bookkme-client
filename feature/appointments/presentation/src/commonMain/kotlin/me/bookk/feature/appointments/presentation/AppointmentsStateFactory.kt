package me.bookk.feature.appointments.presentation

import me.bookk.designsystem.uistate.PickerFieldState
import me.bookk.designsystem.uistate.SimplePickerPresentation
import me.bookk.feature.appointments.domain.api.entity.ServiceSnapshot
import me.bookk.feature.appointments.presentation.screen.create.AppointmentCreateState
import me.bookk.feature.appointments.presentation.screen.requestlist.AppointmentListState

interface AppointmentsStateFactory {
    fun createAppointmentListState(): AppointmentListState
    fun createAppointmentCreateState(): AppointmentCreateState
    fun createServicePickerItemState(): PickerFieldState<SimplePickerPresentation<ServiceSnapshot>>
}
