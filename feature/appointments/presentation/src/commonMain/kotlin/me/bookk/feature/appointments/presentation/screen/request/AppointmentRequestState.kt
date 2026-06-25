package me.bookk.feature.appointments.presentation.screen.request

import me.bookk.designsystem.uistate.AppBarState
import me.bookk.designsystem.uistate.NavigationState
import me.bookk.designsystem.uistate.PresentationNotificationState

interface AppointmentRequestState {
    val appBar: AppBarState
    val notifications: PresentationNotificationState
    val navigation: NavigationState<AppointmentRequestDestinations>
}
