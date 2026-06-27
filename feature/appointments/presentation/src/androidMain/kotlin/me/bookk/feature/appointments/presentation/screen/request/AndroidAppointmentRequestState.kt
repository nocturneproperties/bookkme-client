package me.bookk.feature.appointments.presentation.screen.request

import me.bookk.designsystem.uistate.AndroidListState
import me.bookk.designsystem.uistate.AndroidNotificationState
import me.bookk.designsystem.uistate.ListState
import me.bookk.designsystem.uistate.PresentationNotificationState

internal class AndroidAppointmentRequestState : AppointmentRequestState {
    override val requests: ListState<AppointmentRequestItemState> = AndroidListState()
    override val notifications: PresentationNotificationState = AndroidNotificationState()

    override fun createAppointmentRequestItemState(): AppointmentRequestItemState {
        return AndroidAppointmentRequestItemState()
    }
}
