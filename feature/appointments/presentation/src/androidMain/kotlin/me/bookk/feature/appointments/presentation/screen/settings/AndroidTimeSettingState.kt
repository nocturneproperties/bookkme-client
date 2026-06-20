package me.bookk.feature.appointments.presentation.screen.settings

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import me.bookk.designsystem.uistate.AndroidTimePickerFieldState
import me.bookk.designsystem.uistate.AndroidViewState
import me.bookk.designsystem.uistate.TimePickerFieldState

class AndroidTimeSettingState(
    isDeleteAvailable: Boolean = true
) : AndroidViewState(), TimeSettingState {
    override val timeFromPicker: TimePickerFieldState = AndroidTimePickerFieldState()
    override val timeToPicker: TimePickerFieldState = AndroidTimePickerFieldState()
    override var isDeleteAvailable: Boolean by mutableStateOf(isDeleteAvailable)
}
