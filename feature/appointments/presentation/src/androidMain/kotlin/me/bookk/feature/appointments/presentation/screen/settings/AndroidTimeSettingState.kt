package me.bookk.feature.appointments.presentation.screen.settings

import me.bookk.designsystem.uistate.AndroidTimePickerFieldState
import me.bookk.designsystem.uistate.AndroidViewState
import me.bookk.designsystem.uistate.TimePickerFieldState

class AndroidTimeSettingState: AndroidViewState(), TimeSettingState {
    override val timeFromPicker: TimePickerFieldState = AndroidTimePickerFieldState()
    override val timeToPicker: TimePickerFieldState = AndroidTimePickerFieldState()
}
