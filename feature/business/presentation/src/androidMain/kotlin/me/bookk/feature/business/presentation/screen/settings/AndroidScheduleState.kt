package me.bookk.feature.business.presentation.screen.settings

import me.bookk.designsystem.uistate.AndroidListState
import me.bookk.designsystem.uistate.ListState
import me.bookk.feature.business.presentation.screen.settings.state.DaySettingsState
import me.bookk.feature.business.presentation.screen.settings.state.ScheduleState

internal class AndroidScheduleState : ScheduleState {
    override val monday: DaySettingsState = AndroidDaySettingsState()
    override val tuesday: DaySettingsState = AndroidDaySettingsState()
    override val wednesday: DaySettingsState = AndroidDaySettingsState()
    override val thursday: DaySettingsState = AndroidDaySettingsState()
    override val friday: DaySettingsState = AndroidDaySettingsState()
    override val saturday: DaySettingsState = AndroidDaySettingsState()
    override val sunday: DaySettingsState = AndroidDaySettingsState()

    override val list: ListState<DaySettingsState> = AndroidListState()
}
