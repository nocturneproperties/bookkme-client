package me.bookk.feature.appointments.presentation.screen.settings

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import dev.icerock.moko.resources.desc.StringDesc
import me.bookk.designsystem.uistate.AndroidButtonState
import me.bookk.designsystem.uistate.ButtonState

class AndroidDaySettingsState(
    override val title: StringDesc,
    isActive: Boolean = false
) : DaySettingsState {
    override var isActive: Boolean by mutableStateOf(isActive)
    override var isExpanded: Boolean by mutableStateOf(isActive)
    override val intervals: MutableList<TimeSettingState> = mutableStateListOf()
    override val addTimeButton: ButtonState = AndroidButtonState()
    override var onDeleteInterval: () -> Unit by mutableStateOf({})

    override fun replaceIntervals(newIntervals: List<TimeSettingState>) {
        intervals.clear()
        intervals.addAll(newIntervals)
    }
}
