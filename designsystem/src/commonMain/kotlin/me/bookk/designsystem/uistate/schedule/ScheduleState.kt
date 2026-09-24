package me.bookk.designsystem.uistate.schedule

import dev.icerock.moko.resources.desc.StringDesc
import me.bookk.designsystem.uistate.BooleanState
import me.bookk.designsystem.uistate.ButtonState
import me.bookk.designsystem.uistate.DateRangePickerState
import me.bookk.designsystem.uistate.ListState
import me.bookk.designsystem.uistate.MultiPickerState
import me.bookk.designsystem.uistate.TimePickerFieldState
import me.bookk.designsystem.uistate.ViewState

interface ScheduleState {
    val monday: DaySettingsState
    val tuesday: DaySettingsState
    val wednesday: DaySettingsState
    val thursday: DaySettingsState
    val friday: DaySettingsState
    val saturday: DaySettingsState
    val sunday: DaySettingsState

    val list: ListState<DaySettingsState>

    val dayOffs: MultiPickerState<DateRangePickerPresentation>
    val dateRange: DateRangePickerState
}

interface DaySettingsState : ViewState {
    var isActive: BooleanState
    var dayIndicator: StringDesc
    var title: StringDesc
    val intervals: List<TimeSettingState>
    val addTimeButton: ButtonState
    var onDeleteInterval: (TimeSettingState) -> Unit

    fun replaceIntervals(newIntervals: List<TimeSettingState>)
    fun createTimeSettingState(): TimeSettingState
}

interface TimeSettingState : ViewState {
    val timeFromPicker: TimePickerFieldState
    val timeToPicker: TimePickerFieldState
}
