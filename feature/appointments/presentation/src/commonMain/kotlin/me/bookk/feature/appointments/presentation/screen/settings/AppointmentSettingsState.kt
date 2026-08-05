package me.bookk.feature.appointments.presentation.screen.settings

import me.bookk.designsystem.uistate.AppBarState
import me.bookk.designsystem.uistate.BooleanState
import me.bookk.designsystem.uistate.ButtonState
import me.bookk.designsystem.uistate.NavigationState
import me.bookk.designsystem.uistate.PresentationNotificationState
import me.bookk.designsystem.uistate.TextFieldState

interface AppointmentSettingsState {
    val appBar: AppBarState
    val automaticApproval: BooleanState
    val note: TextFieldState
    val minimalBreak: TextFieldState
    val save: ButtonState

    val notifications: PresentationNotificationState
    val navigation: NavigationState<AppointmentSettingsDestination>
}
