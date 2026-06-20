package me.bookk.feature.appointments.presentation.screen.settings

import me.bookk.designsystem.uistate.AppBarState
import me.bookk.designsystem.uistate.ButtonState
import me.bookk.designsystem.uistate.NavigationState
import me.bookk.designsystem.uistate.PresentationNotificationState

interface AppointmentSettingsState {
    val appBar: AppBarState

    val save: ButtonState

    val notifications: PresentationNotificationState
    val navigation: NavigationState<AppointmentSettingsDestination>
}
