package me.bookk.designsystem.uistate.schedule

import dev.icerock.moko.resources.desc.desc
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime
import kotlinx.datetime.isoDayNumber
import me.bookk.core.presentation.date.DateLocalizer
import me.bookk.core.presentation.date.DateStyle
import me.bookk.core.presentation.date.atNextWeekDay
import me.bookk.core.presentation.date.startOfWeek
import me.bookk.core.presentation.date.today
import me.bookk.core.presentation.memory.weakSelfClosure
import me.bookk.designsystem.resources.DesignSystem

class ScheduleBinder(
    private val state: ScheduleState,
    dateLocalizer: DateLocalizer,
    private val onChanged: () -> Unit
) {
    private val shortWeekdayFormat = dateLocalizer.forStyle(DateStyle.NARROW_WEEKDAY)
    private val fullWeekdayFormat = dateLocalizer.forStyle(DateStyle.FULL_WEEKDAY)
    private val fullDateFormat = dateLocalizer.forStyle(DateStyle.D_MMM_YYYY_RELATIVE)

    var isEditable: Boolean = true
        set(value) {
            field = value
            applyEditable()
        }

    init {
        setupDayOffs()
    }

    fun render(schedule: WeekSchedule) {
        state.dayOffs.replaceSelected(
            schedule.dayOffs.map { DateRangePickerPresentation(it.start, it.end, fullDateFormat) }
        )
        state.dayOffs.onItemsRemoveRequested = weakSelfClosure { binder, items ->
            binder.state.dayOffs.replaceSelected(binder.state.dayOffs.selectedItems.minus(items.toSet()))
            binder.onChanged()
        }
        val byDay = schedule.days.associateBy { it.dayOfWeek }
        DayOfWeek.entries.forEach { dayOfWeek ->
            renderDay(state.dayOf(dayOfWeek), byDay[dayOfWeek] ?: WeekdaySchedule(dayOfWeek, false, emptyList()))
        }
        state.list.replace(weekOrderedDays())
        applyEditable()
    }

    private fun applyEditable() {
        state.isEditable = isEditable
        state.dayOffs.isEditable = isEditable
        DayOfWeek.entries.forEach { dayOfWeek ->
            val day = state.dayOf(dayOfWeek)
            day.isActive.isEnabled = isEditable
            day.intervals.forEach { it.applyEditable() }
        }
    }

    private fun TimeSettingState.applyEditable() {
        timeFromPicker.textField.enabled = isEditable
        timeToPicker.textField.enabled = isEditable
    }

    fun snapshot(): WeekSchedule {
        return WeekSchedule(
            days = DayOfWeek.entries.map { state.dayOf(it).toWeekdaySchedule(it) },
            dayOffs = state.dayOffs.selectedItems.map { DayOffPeriod(it.dateFrom, it.dateTo) }
        )
    }

    private fun weekOrderedDays(): List<DaySettingsState> {
        val days = DayOfWeek.entries.map { state.dayOf(it) }
        val firstDayOffset = LocalDate.today().startOfWeek().dayOfWeek.isoDayNumber - 1
        return days.drop(firstDayOffset) + days.take(firstDayOffset)
    }

    private fun DaySettingsState.toWeekdaySchedule(dayOfWeek: DayOfWeek): WeekdaySchedule {
        return WeekdaySchedule(
            dayOfWeek = dayOfWeek,
            isActive = isActive.isChecked,
            workingHours = intervals.mapNotNull { interval ->
                val from = interval.timeFromPicker.timePicker.pickedTime
                val to = interval.timeToPicker.timePicker.pickedTime
                if (from != null && to != null) WorkingHours(from, to) else null
            }
        )
    }

    private fun renderDay(day: DaySettingsState, daySchedule: WeekdaySchedule) {
        val dayOfWeek = daySchedule.dayOfWeek
        day.id = dayOfWeek.hashCode().toString()
        day.isActive.isChecked = daySchedule.isActive
        day.isActive.onCheckedChange = weakSelfClosure { binder, isChecked: Boolean ->
            binder.state.dayOf(dayOfWeek).isActive.isChecked = isChecked
            binder.onChanged()
        }
        val weekday = LocalDate.atNextWeekDay(dayOfWeek)
        day.dayIndicator = shortWeekdayFormat.format(weekday).desc()
        day.title = fullWeekdayFormat.format(weekday).desc()
        day.addTimeButton.icon = DesignSystem.images.plus
        day.addTimeButton.text = DesignSystem.strings.schedule_add_working_time.desc()
        day.addTimeButton.onClick = weakSelfClosure { binder ->
            val current = binder.state.dayOf(dayOfWeek)
            current.replaceIntervals(current.intervals + binder.renderInterval(current.createTimeSettingState(), null))
            binder.onChanged()
        }
        day.onDeleteInterval = weakSelfClosure { binder, interval: TimeSettingState ->
            val current = binder.state.dayOf(dayOfWeek)
            current.replaceIntervals(current.intervals - interval)
            binder.onChanged()
        }
        day.replaceIntervals(daySchedule.workingHours.map { renderInterval(day.createTimeSettingState(), it) })
    }

    private fun renderInterval(interval: TimeSettingState, hours: WorkingHours?): TimeSettingState {
        interval.timeFromPicker.textField.placeholder = DesignSystem.strings.common_from.desc()
        interval.timeToPicker.textField.placeholder = DesignSystem.strings.common_to.desc()
        interval.timeFromPicker.textField.text = hours?.let { fullWeekdayFormat.format(it.from) }.orEmpty()
        interval.timeToPicker.textField.text = hours?.let { fullWeekdayFormat.format(it.to) }.orEmpty()
        interval.timeFromPicker.timePicker.pickedTime = hours?.from
        interval.timeToPicker.timePicker.pickedTime = hours?.to

        val format = fullWeekdayFormat
        val notifyChanged = weakSelfClosure { binder -> binder.onChanged() }
        val setFrom = interval.weakSelfClosure { it, time: LocalTime ->
            it.timeFromPicker.textField.text = format.format(time)
            it.timeFromPicker.timePicker.pickedTime = time
        }
        interval.timeFromPicker.timePicker.onTimePicked = { time ->
            setFrom(time)
            notifyChanged()
        }
        val setTo = interval.weakSelfClosure { it, time: LocalTime ->
            it.timeToPicker.textField.text = format.format(time)
            it.timeToPicker.timePicker.pickedTime = time
        }
        interval.timeToPicker.timePicker.onTimePicked = { time ->
            setTo(time)
            notifyChanged()
        }
        return interval
    }

    private fun setupDayOffs() {
        state.dayOffs.pickerTitle = DesignSystem.strings.schedule_day_offs.desc()
        state.dayOffs.placeholder = DesignSystem.strings.schedule_no_day_offs.desc()
        state.dayOffs.addItemButton.text = DesignSystem.strings.schedule_add_day_off.desc()
        state.dayOffs.addItemButton.icon = DesignSystem.images.plus

        val dateRange = state.dateRange
        val format = fullDateFormat
        dateRange.title = DesignSystem.strings.schedule_pick_day_off_title.desc()
        dateRange.startDate.textField.placeholder = DesignSystem.strings.common_from.desc()
        dateRange.endDate.textField.placeholder = DesignSystem.strings.common_to.desc()
        dateRange.startDate.datePicker.minDate = LocalDate.today()
        dateRange.startDate.datePicker.onDatePicked = weakSelfClosure { binder, date: LocalDate ->
            val range = binder.state.dateRange
            range.startDate.datePicker.pickedDate = date
            range.startDate.textField.text = format.format(date)
            range.endDate.datePicker.minDate = date
        }
        dateRange.endDate.datePicker.onDatePicked = weakSelfClosure { binder, date: LocalDate ->
            val range = binder.state.dateRange
            range.endDate.datePicker.pickedDate = date
            range.endDate.textField.text = format.format(date)
            range.startDate.datePicker.maxDate = date
        }
        dateRange.onDateRangeSelected = weakSelfClosure { binder -> binder.addPickedDayOff() }
    }

    private fun addPickedDayOff() {
        val range = state.dateRange
        val startDate = range.startDate.datePicker.pickedDate ?: return
        val endDate = range.endDate.datePicker.pickedDate ?: return
        state.dayOffs.replaceSelected(
            state.dayOffs.selectedItems + DateRangePickerPresentation(startDate, endDate, fullDateFormat)
        )
        range.startDate.datePicker.pickedDate = null
        range.startDate.textField.text = ""
        range.startDate.datePicker.maxDate = null
        range.endDate.datePicker.pickedDate = null
        range.endDate.textField.text = ""
        range.endDate.datePicker.minDate = null
        onChanged()
    }

    private fun ScheduleState.dayOf(dayOfWeek: DayOfWeek): DaySettingsState {
        return when (dayOfWeek) {
            DayOfWeek.MONDAY -> monday
            DayOfWeek.TUESDAY -> tuesday
            DayOfWeek.WEDNESDAY -> wednesday
            DayOfWeek.THURSDAY -> thursday
            DayOfWeek.FRIDAY -> friday
            DayOfWeek.SATURDAY -> saturday
            DayOfWeek.SUNDAY -> sunday
        }
    }
}
