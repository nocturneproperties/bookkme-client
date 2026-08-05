package me.bookk.feature.business.presentation.screen.settings

import me.bookk.designsystem.uistate.AndroidTimePickerFieldState
import me.bookk.designsystem.uistate.AndroidViewState
import me.bookk.designsystem.uistate.TimePickerFieldState
import me.bookk.feature.business.presentation.screen.settings.state.TimeSettingState

class AndroidTimeSettingState : AndroidViewState(), TimeSettingState {
    override val timeFromPicker: TimePickerFieldState = AndroidTimePickerFieldState()
    override val timeToPicker: TimePickerFieldState = AndroidTimePickerFieldState()
}
