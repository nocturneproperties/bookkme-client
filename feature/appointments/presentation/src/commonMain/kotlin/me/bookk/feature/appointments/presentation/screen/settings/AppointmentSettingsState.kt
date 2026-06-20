package me.bookk.feature.appointments.presentation.screen.settings

import dev.icerock.moko.resources.desc.StringDesc
import me.bookk.designsystem.uistate.AppBarState
import me.bookk.designsystem.uistate.BooleanState
import me.bookk.designsystem.uistate.ButtonState
import me.bookk.designsystem.uistate.MinimalPickerPresentation
import me.bookk.designsystem.uistate.MultiPickerState
import me.bookk.designsystem.uistate.NavigationState
import me.bookk.designsystem.uistate.PresentationNotificationState
import me.bookk.designsystem.uistate.TimePickerFieldState
import me.bookk.designsystem.uistate.ViewState

interface AppointmentSettingsState {
    val appBar: AppBarState
    val automaticApproval: BooleanState
    val dayOffs: MultiPickerState<MinimalPickerPresentation>
    val schedule: List<DaySettingsState>
    val save: ButtonState

    val notifications: PresentationNotificationState
    val navigation: NavigationState<AppointmentSettingsDestination>
}

interface DaySettingsState {
    var isActive: Boolean
    var isExpanded: Boolean
    val title: StringDesc
    val intervals: List<TimeSettingState>
    val addTimeButton: ButtonState
    var onDeleteInterval: () -> Unit

    fun replaceIntervals(newIntervals: List<TimeSettingState>)
}

interface TimeSettingState : ViewState {
    val timeFromPicker: TimePickerFieldState
    val timeToPicker: TimePickerFieldState
    var isDeleteAvailable: Boolean
}
