package me.bookk.feature.appointments.presentation.screen.settings

import me.bookk.designsystem.uistate.AndroidAppBarState
import me.bookk.designsystem.uistate.AndroidBooleanState
import me.bookk.designsystem.uistate.AndroidButtonState
import me.bookk.designsystem.uistate.AndroidNavigationState
import me.bookk.designsystem.uistate.AndroidNotificationState
import me.bookk.designsystem.uistate.AndroidTextFieldState
import me.bookk.designsystem.uistate.AppBarState
import me.bookk.designsystem.uistate.BooleanState
import me.bookk.designsystem.uistate.ButtonState
import me.bookk.designsystem.uistate.PresentationNotificationState
import me.bookk.designsystem.uistate.TextFieldState

internal class AndroidAppointmentSettingsState : AppointmentSettingsState {
    override val appBar: AppBarState = AndroidAppBarState()
    override val automaticApproval: BooleanState = AndroidBooleanState()
    override val note: TextFieldState = AndroidTextFieldState()
    override val minimalBreak: TextFieldState = AndroidTextFieldState()
    override val save: ButtonState = AndroidButtonState()

    override val notifications: PresentationNotificationState = AndroidNotificationState()
    override val navigation = AndroidNavigationState<AppointmentSettingsDestination>()
}
