package me.bookk.feature.appointments.presentation.screen.request

import me.bookk.designsystem.uistate.ButtonState
import me.bookk.designsystem.uistate.ListState
import me.bookk.designsystem.uistate.PresentationNotificationState
import me.bookk.designsystem.uistate.ViewState

interface AppointmentRequestState {
    val requests: ListState<AppointmentRequestItemState>
    val notifications: PresentationNotificationState

    fun createAppointmentRequestItemState(): AppointmentRequestItemState
}

interface AppointmentRequestItemState: ViewState {
    var clientName: String
    var serviceName: String
    var scheduledDate: String
    var scheduledTime: String
    var note: String
    var earnings: String
    val approveButton: ButtonState
    val declineButton: ButtonState
}
