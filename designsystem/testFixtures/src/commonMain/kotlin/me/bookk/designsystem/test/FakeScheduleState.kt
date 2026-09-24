package me.bookk.designsystem.test

import dev.icerock.moko.resources.desc.StringDesc
import dev.icerock.moko.resources.desc.desc
import me.bookk.designsystem.uistate.BooleanState
import me.bookk.designsystem.uistate.schedule.DateRangePickerPresentation
import me.bookk.designsystem.uistate.schedule.DaySettingsState
import me.bookk.designsystem.uistate.schedule.ScheduleState
import me.bookk.designsystem.uistate.schedule.TimeSettingState

class FakeScheduleState : ScheduleState {
    override val monday = FakeDaySettingsState()
    override val tuesday = FakeDaySettingsState()
    override val wednesday = FakeDaySettingsState()
    override val thursday = FakeDaySettingsState()
    override val friday = FakeDaySettingsState()
    override val saturday = FakeDaySettingsState()
    override val sunday = FakeDaySettingsState()
    override val list = FakeListState<DaySettingsState>()
    override val dayOffs = FakeMultiPickerState<DateRangePickerPresentation>()
    override val dateRange = FakeDateRangePickerState()
}

class FakeDaySettingsState : FakeViewState(), DaySettingsState {
    override var isActive: BooleanState = FakeBooleanState()
    override var dayIndicator: StringDesc = "".desc()
    override var title: StringDesc = "".desc()
    override val intervals: MutableList<TimeSettingState> = mutableListOf()
    override val addTimeButton = FakeButtonState()
    override var onDeleteInterval: (TimeSettingState) -> Unit = {}

    override fun replaceIntervals(newIntervals: List<TimeSettingState>) {
        intervals.clear()
        intervals += newIntervals
    }

    override fun createTimeSettingState(): TimeSettingState {
        return FakeTimeSettingState()
    }
}

class FakeTimeSettingState : FakeViewState(), TimeSettingState {
    override val timeFromPicker = FakeTimePickerFieldState()
    override val timeToPicker = FakeTimePickerFieldState()
}
