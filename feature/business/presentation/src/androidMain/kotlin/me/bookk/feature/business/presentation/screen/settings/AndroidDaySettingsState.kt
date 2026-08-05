package me.bookk.feature.business.presentation.screen.settings

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import dev.icerock.moko.resources.desc.StringDesc
import dev.icerock.moko.resources.desc.desc
import me.bookk.designsystem.uistate.AndroidBooleanState
import me.bookk.designsystem.uistate.AndroidButtonState
import me.bookk.designsystem.uistate.AndroidViewState
import me.bookk.designsystem.uistate.BooleanState
import me.bookk.designsystem.uistate.ButtonState
import me.bookk.feature.business.presentation.screen.settings.state.DaySettingsState
import me.bookk.feature.business.presentation.screen.settings.state.TimeSettingState

class AndroidDaySettingsState : AndroidViewState(isVisible = false), DaySettingsState {

    override var title: StringDesc by mutableStateOf("".desc())
    override var dayIndicator: StringDesc by mutableStateOf("".desc())

    override var isActive: BooleanState = AndroidBooleanState()
    override val intervals: MutableList<TimeSettingState> = mutableStateListOf()
    override val addTimeButton: ButtonState = AndroidButtonState()
    override var onDeleteInterval: (TimeSettingState) -> Unit by mutableStateOf({})

    override fun replaceIntervals(newIntervals: List<TimeSettingState>) {
        intervals.clear()
        intervals.addAll(newIntervals)
    }

    override fun createTimeSettingState(): TimeSettingState {
        return AndroidTimeSettingState()
    }
}
