package me.bookk.feature.appointments.presentation.screen.settings

import androidx.compose.runtime.mutableStateListOf
import me.bookk.designsystem.uistate.AndroidAppBarState
import me.bookk.designsystem.uistate.AndroidBooleanState
import me.bookk.designsystem.uistate.AndroidButtonState
import me.bookk.designsystem.uistate.AndroidMultiPickerState
import me.bookk.designsystem.uistate.AndroidNavigationState
import me.bookk.designsystem.uistate.AndroidNotificationState
import me.bookk.designsystem.uistate.AppBarState
import me.bookk.designsystem.uistate.BooleanState
import me.bookk.designsystem.uistate.ButtonState
import me.bookk.designsystem.uistate.MinimalPickerPresentation
import me.bookk.designsystem.uistate.MultiPickerState
import me.bookk.designsystem.uistate.NavigationState
import me.bookk.designsystem.uistate.PresentationNotificationState

internal class AndroidAppointmentSettingsState : AppointmentSettingsState {
    override val appBar: AppBarState = AndroidAppBarState()
    override val automaticApproval: BooleanState = AndroidBooleanState()
    override val schedule: List<DaySettingsState> = mutableStateListOf()
    override val dayOffs: MultiPickerState<MinimalPickerPresentation> = AndroidMultiPickerState()

    override val save: ButtonState = AndroidButtonState()

    override val notifications: PresentationNotificationState = AndroidNotificationState()
    override val navigation: NavigationState<AppointmentSettingsDestination> = AndroidNavigationState()
}
