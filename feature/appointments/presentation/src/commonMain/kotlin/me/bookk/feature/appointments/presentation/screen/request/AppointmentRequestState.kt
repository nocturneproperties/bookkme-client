package me.bookk.feature.appointments.presentation.screen.request

import dev.icerock.moko.resources.desc.StringDesc
import me.bookk.designsystem.uistate.ButtonState
import me.bookk.designsystem.uistate.ListState
import me.bookk.designsystem.uistate.PresentationNotificationState
import me.bookk.designsystem.uistate.ViewState

interface AppointmentRequestState {
    var title: StringDesc
    val requests: ListState<AppointmentRequestItemState>
    val notifications: PresentationNotificationState

    fun createAppointmentRequestItemState(): AppointmentRequestItemState
}

interface AppointmentRequestItemState : ViewState {
    var clientName: String
    var clientInitials: String
    var schedule: StringDesc
    var services: List<StringDesc>
    var price: String
    var note: String
    var onCallClick: (() -> Unit)?
    val approveButton: ButtonState
    val declineButton: ButtonState
}
