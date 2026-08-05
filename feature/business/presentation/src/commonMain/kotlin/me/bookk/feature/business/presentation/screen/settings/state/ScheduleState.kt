package me.bookk.feature.business.presentation.screen.settings.state

import dev.icerock.moko.resources.desc.StringDesc
import dev.icerock.moko.resources.desc.desc
import kotlinx.datetime.LocalDate
import me.bookk.core.presentation.date.DateLocalizer
import me.bookk.designsystem.uistate.BooleanState
import me.bookk.designsystem.uistate.ButtonState
import me.bookk.designsystem.uistate.ListState
import me.bookk.designsystem.uistate.PickerPresentation
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

data class DateRangePickerPresentation(
    val dateFrom: LocalDate,
    val dateTo: LocalDate,
    override val displayName: StringDesc
) : PickerPresentation() {
    override val pickerItemId: String = dateFrom.toString() + dateTo.toString()

    constructor(dateFrom: LocalDate, dateTo: LocalDate, formatter: DateLocalizer.Formatter) : this(
        dateFrom = dateFrom,
        dateTo = dateTo,
        displayName = buildString {
            append(formatter.format(dateFrom, relative = true))
            if (dateFrom != dateTo) {
                append(" - ")
                append(formatter.format(dateTo, relative = true))
            }
        }.desc()
    )
}
