package me.bookk.designsystem.uistate.schedule

import me.bookk.designsystem.uistate.AndroidDateRangePickerState
import me.bookk.designsystem.uistate.AndroidListState
import me.bookk.designsystem.uistate.AndroidMultiPickerState
import me.bookk.designsystem.uistate.DateRangePickerState
import me.bookk.designsystem.uistate.ListState
import me.bookk.designsystem.uistate.MultiPickerState

class AndroidScheduleState : ScheduleState {
    override val monday: DaySettingsState = AndroidDaySettingsState()
    override val tuesday: DaySettingsState = AndroidDaySettingsState()
    override val wednesday: DaySettingsState = AndroidDaySettingsState()
    override val thursday: DaySettingsState = AndroidDaySettingsState()
    override val friday: DaySettingsState = AndroidDaySettingsState()
    override val saturday: DaySettingsState = AndroidDaySettingsState()
    override val sunday: DaySettingsState = AndroidDaySettingsState()

    override val list: ListState<DaySettingsState> = AndroidListState()

    override val dayOffs: MultiPickerState<DateRangePickerPresentation> = AndroidMultiPickerState()
    override val dateRange: DateRangePickerState = AndroidDateRangePickerState()
}
