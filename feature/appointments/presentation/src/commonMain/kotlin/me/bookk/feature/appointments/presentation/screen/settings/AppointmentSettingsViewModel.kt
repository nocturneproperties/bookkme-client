package me.bookk.feature.appointments.presentation.screen.settings

import dev.icerock.moko.resources.desc.desc
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDate
import kotlinx.datetime.isoDayNumber
import me.bookk.android.feature.appointments.resources.AppointmentsRes
import me.bookk.core.coroutine.DispatcherProvider
import me.bookk.core.presentation.ViewModel
import me.bookk.core.presentation.VmArgs
import me.bookk.core.presentation.date.DateLocalizer
import me.bookk.core.presentation.date.DateStyle
import me.bookk.core.presentation.date.atNextWeekDay
import me.bookk.core.presentation.date.startOfWeek
import me.bookk.core.presentation.date.today
import me.bookk.core.presentation.error.PresentationNotification
import me.bookk.core.presentation.memory.weakSelfClosure
import me.bookk.core.presentation.memory.weakVMClosure
import me.bookk.designsystem.resources.DesignSystem
import me.bookk.designsystem.simple
import me.bookk.designsystem.uistate.InputType
import me.bookk.designsystem.uistate.TopBarSize
import me.bookk.designsystem.uistate.startLoading
import me.bookk.designsystem.uistate.stopLoading
import me.bookk.feature.appointments.domain.api.GetAppointmentSettings
import me.bookk.feature.appointments.domain.api.UpdateAppointmentSettings
import me.bookk.feature.appointments.domain.api.entity.AppointmentSettings
import me.bookk.feature.appointments.domain.api.entity.DayOfWeekSchedule
import me.bookk.feature.appointments.domain.api.entity.DayOffRange
import me.bookk.feature.appointments.domain.api.entity.WorkHour
import me.bookk.feature.appointments.domain.api.entity.WorkingSchedule
import me.bookk.feature.appointments.presentation.AppointmentsStateFactory
import org.koin.core.annotation.InjectedParam
import kotlin.uuid.Uuid

class AppointmentSettingsViewModel(
    @InjectedParam private val businessId: Uuid,
    private val getAppointmentSettings: GetAppointmentSettings,
    private val updateAppointmentSettings: UpdateAppointmentSettings,
    dateLocalizer: DateLocalizer,
    stateFactory: AppointmentsStateFactory,
    vmArgs: VmArgs
) : ViewModel(vmArgs) {

    private val shortWeekdayFormat = dateLocalizer.forStyle(DateStyle.NARROW_WEEKDAY)
    private val fullWeekdayFormat = dateLocalizer.forStyle(DateStyle.FULL_WEEKDAY)
    private val fullDateFormat = dateLocalizer.forStyle(DateStyle.D_MMM_YYYY_RELATIVE)
    private var loadedSettings = AppointmentSettings.stub(businessId = businessId)

    val uiState: AppointmentSettingsState = stateFactory.createAppointmentSettingsState().setup()

    init {
        loadSettings()
    }

    private fun loadSettings() {
        launchCached(
            launchIn = DispatcherProvider.io,
            call = { getAppointmentSettings.cached(businessId, it) },
            onComplete = {
                loadedSettings = it
                renderSettings(it)
            },
            onError = { uiState.notifications.add(it.notification()) }
        )
    }

    private fun onSaveClick() {
        val appointmentSettings = snapshotState()
        launch(
            launchIn = DispatcherProvider.io,
            onStart = { uiState.save.startLoading() },
            call = { updateAppointmentSettings(appointmentSettings) },
            onComplete = ::renderSettings,
            onError = {
                when (it) {
                    is UpdateAppointmentSettings.Error.ActiveDayWithoutWorkHours ->
                        uiState.notifications.add(
                            PresentationNotification.Message.simple(
                                AppointmentsRes.strings.appointments_settings_active_day_without_work_hours_error.desc()
                            )
                        )
                    is UpdateAppointmentSettings.Error.InvalidDayOffRange ->
                        uiState.notifications.add(
                            PresentationNotification.Message.simple(
                                AppointmentsRes.strings.appointments_settings_invalid_day_off_range_error.desc()
                            )
                        )
                    else -> uiState.notifications.add(it.notification())
                }
            },
            onTerminate = { uiState.save.stopLoading() }
        )
    }

    private fun snapshotState(): AppointmentSettings = with(uiState.schedule) {
        loadedSettings.copy(
            schedule = WorkingSchedule(
                listOf(
                    monday.toDomain(DayOfWeek.MONDAY),
                    tuesday.toDomain(DayOfWeek.TUESDAY),
                    wednesday.toDomain(DayOfWeek.WEDNESDAY),
                    thursday.toDomain(DayOfWeek.THURSDAY),
                    friday.toDomain(DayOfWeek.FRIDAY),
                    saturday.toDomain(DayOfWeek.SATURDAY),
                    sunday.toDomain(DayOfWeek.SUNDAY)
                ).associateBy { it.dayOfWeek }
            ),
            dayOffs = uiState.dayOffs.selectedItems.map { DayOffRange(it.dateFrom, it.dateTo) },
            automaticApproval = uiState.automaticApproval.isChecked,
            inBetweenBreakInMinutes = uiState.minimalBreak.text.toIntOrNull() ?: 10,
            appointmentNote = uiState.note.text
        )
    }

    private fun DaySettingsState.toDomain(dayOfWeek: DayOfWeek) = DayOfWeekSchedule(
        dayOfWeek = dayOfWeek,
        workingTime = intervals.map {
            WorkHour(
                dayOfWeek,
                it.timeFromPicker.timePicker.pickedTime!!,
                it.timeToPicker.timePicker.pickedTime!!
            )
        },
        isActive = isActive.isChecked
    )

    private fun renderSettings(settings: AppointmentSettings) = with(uiState) {
        automaticApproval.isChecked = settings.automaticApproval

        dayOffs.replaceSelected(
            settings.dayOffs.map {
                DateRangePickerPresentation(
                    dateFrom = it.start,
                    dateTo = it.end,
                    formatter = fullDateFormat
                )
            }
        )
        dayOffs.onItemsRemoveRequested = {
            dayOffs.replaceSelected(dayOffs.selectedItems.minus(it))
        }
        schedule.monday.render(settings.schedule.monday)
        schedule.tuesday.render(settings.schedule.tuesday)
        schedule.wednesday.render(settings.schedule.wednesday)
        schedule.thursday.render(settings.schedule.thursday)
        schedule.friday.render(settings.schedule.friday)
        schedule.saturday.render(settings.schedule.saturday)
        schedule.sunday.render(settings.schedule.sunday)

        val days = mutableListOf(
            schedule.monday,
            schedule.tuesday,
            schedule.wednesday,
            schedule.thursday,
            schedule.friday,
            schedule.saturday,
            schedule.sunday
        )
        val firstDayOfWeek = LocalDate.today().startOfWeek().dayOfWeek
        for (i in 0 until (firstDayOfWeek.isoDayNumber - 1)) {
            days.add(days.removeAt(i))
        }

        schedule.list.replace(days)

        note.text = settings.appointmentNote

        minimalBreak.text = settings.inBetweenBreakInMinutes.toString()
    }

    private fun DaySettingsState.render(schedule: DayOfWeekSchedule): DaySettingsState {
        id = schedule.dayOfWeek.hashCode().toString()
        isActive.isChecked = schedule.isActive
        isActive.onCheckedChange = weakSelfClosure { state, isChecked ->
            state.isActive.isChecked = isChecked
        }
        val weekday = LocalDate.atNextWeekDay(schedule.dayOfWeek)
        dayIndicator = shortWeekdayFormat.format(weekday).desc()
        title = fullWeekdayFormat.format(weekday).desc()
        addTimeButton.icon = DesignSystem.images.plus
        addTimeButton.text = AppointmentsRes.strings.appointments_settings_add_working_time.desc()
        addTimeButton.onClick = weakSelfClosure {
            it.replaceIntervals(it.intervals + it.createTimeSettingState().render())
        }
        onDeleteInterval = weakSelfClosure { state, interval ->
            state.replaceIntervals(state.intervals - interval)
        }
        replaceIntervals(
            schedule.workingTime.map { time ->
                createTimeSettingState().render(time)
            }
        )

        return this
    }

    private fun TimeSettingState.render(hour: WorkHour? = null): TimeSettingState {
        timeFromPicker.textField.placeholder = DesignSystem.strings.common_from.desc()
        timeToPicker.textField.placeholder = DesignSystem.strings.common_to.desc()

        timeFromPicker.textField.text = hour?.let { fullWeekdayFormat.format(it.from) }.orEmpty()
        timeToPicker.textField.text = hour?.let { fullWeekdayFormat.format(it.to) }.orEmpty()

        timeFromPicker.timePicker.pickedTime = hour?.from
        timeToPicker.timePicker.pickedTime = hour?.to

        timeFromPicker.timePicker.onTimePicked = weakSelfClosure { state, time ->
            state.timeFromPicker.textField.text = fullWeekdayFormat.format(time)
            state.timeFromPicker.timePicker.pickedTime = time
        }

        timeToPicker.timePicker.onTimePicked = weakSelfClosure { state, time ->
            state.timeToPicker.textField.text = fullWeekdayFormat.format(time)
            state.timeToPicker.timePicker.pickedTime = time
        }
        return this
    }

    private fun AppointmentSettingsState.setup() = apply {
        setupAppBar()
        setupAutomaticApproval()
        setupDayOffs()
        setupMinimalBreak()
        setupNote()
        setupSaveButton()
    }

    private fun AppointmentSettingsState.setupSaveButton() {
        save.text = DesignSystem.strings.action_save.desc()
        save.onClick = weakVMClosure { it.onSaveClick() }
    }

    private fun AppointmentSettingsState.setupNote() {
        note.placeholder = AppointmentsRes.strings.appointments_settings_note_placeholder.desc()
        note.maxLength = 2048
        note.onTextChanged = weakSelfClosure { state, v -> state.note.text = v }
    }

    private fun AppointmentSettingsState.setupAutomaticApproval() {
        automaticApproval.text =
            AppointmentsRes.strings.appointments_settings_automatic_approval.desc()
        automaticApproval.onCheckedChange = weakVMClosure { vm, v ->
            vm.uiState.automaticApproval.isChecked = v
        }
    }

    private fun AppointmentSettingsState.setupAppBar() {
        appBar.size = TopBarSize.SMALL
        appBar.title = AppointmentsRes.strings.appointments_settings_title.desc()
        appBar.onBackClick = weakVMClosure {
            it.uiState.navigation.push(AppointmentSettingsDestination.Back)
        }
    }

    private fun AppointmentSettingsState.setupMinimalBreak() {
        minimalBreak.label = AppointmentsRes.strings.appointments_settings_minimal_break.desc()
        minimalBreak.suffix = DesignSystem.strings.common_min.desc()
        minimalBreak.inputType = InputType.DIGIT
        minimalBreak.onTextChanged = weakSelfClosure { state, v ->
            state.minimalBreak.text = v.filter { it.isDigit() }
        }
        minimalBreak.supportingTextRes = AppointmentsRes.strings.appointments_settings_break_footer.desc()
    }

    private fun AppointmentSettingsState.setupDayOffs() {
        dayOffs.pickerTitle = AppointmentsRes.strings.appointments_settings_day_offs.desc()
        dayOffs.placeholder = AppointmentsRes.strings.appointments_settings_no_day_offs.desc()
        dayOffs.addItemButton.text = AppointmentsRes.strings.appointments_settings_add_day_off.desc()
        dayOffs.addItemButton.icon = DesignSystem.images.plus

        dateRange.title = AppointmentsRes.strings.appointments_settings_pick_day_off_title.desc()
        dateRange.startDate.textField.placeholder = DesignSystem.strings.common_from.desc()
        dateRange.endDate.textField.placeholder = DesignSystem.strings.common_to.desc()
        dateRange.startDate.datePicker.minDate = LocalDate.today()
        dateRange.startDate.datePicker.onDatePicked = weakSelfClosure { it, date ->
            it.dateRange.startDate.datePicker.pickedDate = date
            it.dateRange.startDate.textField.text = fullDateFormat.format(date)

            it.dateRange.endDate.datePicker.minDate = date
        }
        dateRange.endDate.datePicker.onDatePicked = weakSelfClosure { it, date ->
            it.dateRange.endDate.datePicker.pickedDate = date
            it.dateRange.endDate.textField.text = fullDateFormat.format(date)

            it.dateRange.startDate.datePicker.maxDate = date
        }
        dateRange.onDateRangeSelected = weakSelfClosure {
            val startDate = it.dateRange.startDate.datePicker.pickedDate
            val endDate = it.dateRange.endDate.datePicker.pickedDate
            if (startDate != null && endDate != null) {
                val newItem = DateRangePickerPresentation(
                    dateFrom = startDate,
                    dateTo = endDate,
                    formatter = fullDateFormat
                )
                dayOffs.replaceSelected(dayOffs.selectedItems + newItem)
                it.dateRange.startDate.datePicker.pickedDate = null
                it.dateRange.startDate.textField.text = ""
                it.dateRange.startDate.datePicker.maxDate = null
                it.dateRange.endDate.datePicker.pickedDate = null
                it.dateRange.endDate.textField.text = ""
                it.dateRange.endDate.datePicker.minDate = null
            }
        }
    }
}
