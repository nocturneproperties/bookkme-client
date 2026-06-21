package me.bookk.feature.appointments.presentation.screen.settings

import me.bookk.designsystem.uistate.AndroidAppBarState
import me.bookk.designsystem.uistate.AndroidBooleanState
import me.bookk.designsystem.uistate.AndroidButtonState
import me.bookk.designsystem.uistate.AndroidDateRangePickerState
import me.bookk.designsystem.uistate.AndroidMultiPickerState
import me.bookk.designsystem.uistate.AndroidNavigationState
import me.bookk.designsystem.uistate.AndroidNotificationState
import me.bookk.designsystem.uistate.AndroidTextFieldState
import me.bookk.designsystem.uistate.AppBarState
import me.bookk.designsystem.uistate.BooleanState
import me.bookk.designsystem.uistate.ButtonState
import me.bookk.designsystem.uistate.DateRangePickerState
import me.bookk.designsystem.uistate.MultiPickerState
import me.bookk.designsystem.uistate.PresentationNotificationState
import me.bookk.designsystem.uistate.TextFieldState

internal class AndroidAppointmentSettingsState : AppointmentSettingsState {
    override val appBar: AppBarState = AndroidAppBarState()
    override val automaticApproval: BooleanState = AndroidBooleanState()
    override val schedule: ScheduleState = AndroidScheduleState()
    override val dateRange: DateRangePickerState = AndroidDateRangePickerState()
    override val dayOffs: MultiPickerState<DateRangePickerPresentation> = AndroidMultiPickerState()
    override val note: TextFieldState = AndroidTextFieldState()
    override val minimalBreak: TextFieldState = AndroidTextFieldState()
    override val save: ButtonState = AndroidButtonState()

    override val notifications: PresentationNotificationState = AndroidNotificationState()
    override val navigation = AndroidNavigationState<AppointmentSettingsDestination>()

    override fun createDaySettingState(): DaySettingsState {
        return AndroidDaySettingsState()
    }
}
