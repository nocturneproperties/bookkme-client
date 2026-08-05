package me.bookk.feature.business.presentation.screen.settings

import dev.icerock.moko.resources.desc.desc
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime
import kotlinx.datetime.isoDayNumber
import library.device.api.DeviceFacade
import library.money.api.Currency
import library.money.api.Money
import me.bookk.android.feature.business.resources.BusinessRes
import me.bookk.core.coroutine.DispatcherProvider
import me.bookk.core.presentation.ViewModel
import me.bookk.core.presentation.VmArgs
import me.bookk.core.presentation.date.DateLocalizer
import me.bookk.core.presentation.date.DateStyle
import me.bookk.core.presentation.date.atNextWeekDay
import me.bookk.core.presentation.date.startOfWeek
import me.bookk.core.presentation.date.today
import me.bookk.core.presentation.error.PresentationNotification
import me.bookk.core.presentation.error.PresentationNotification.GlobalMessage
import me.bookk.core.presentation.memory.weakSelfClosure
import me.bookk.core.presentation.memory.weakVMClosure
import me.bookk.designsystem.resources.DesignSystem
import me.bookk.designsystem.resources.asPhone
import me.bookk.designsystem.resources.toOneLine
import me.bookk.designsystem.simple
import me.bookk.designsystem.uistate.ValidationState
import me.bookk.designsystem.uistate.startLoading
import me.bookk.designsystem.uistate.stopLoading
import me.bookk.feature.business.domain.api.business.GetBusinessById
import me.bookk.feature.business.domain.api.business.UpdateBusiness
import me.bookk.feature.business.domain.api.entity.Business
import me.bookk.feature.business.domain.api.entity.Business.Social
import me.bookk.feature.business.domain.api.entity.Business.SocialKind
import me.bookk.feature.business.domain.api.entity.DayOfWeekSchedule
import me.bookk.feature.business.domain.api.entity.DayOffRange
import me.bookk.feature.business.domain.api.entity.WorkHour
import me.bookk.feature.business.domain.api.entity.WorkingSchedule
import me.bookk.feature.business.presentation.BusinessStateFactory
import me.bookk.feature.business.presentation.screen.settings.state.BusinessSettingsDestination
import me.bookk.feature.business.presentation.screen.settings.state.BusinessSettingsState
import me.bookk.feature.business.presentation.screen.settings.state.CurrencyUI
import me.bookk.feature.business.presentation.screen.settings.state.DateRangePickerPresentation
import me.bookk.feature.business.presentation.screen.settings.state.DaySettingsState
import me.bookk.feature.business.presentation.screen.settings.state.ScheduleState
import me.bookk.feature.business.presentation.screen.settings.state.TimeSettingState
import me.bookk.feature.business.presentation.screen.settings.state.toCurrencyUI
import kotlin.properties.Delegates
import kotlin.uuid.Uuid

class BusinessSettingsViewModel(
    private val businessId: Uuid,
    private val deviceFacade: DeviceFacade,
    private val getBusinessById: GetBusinessById,
    private val updateBusiness: UpdateBusiness,
    dateLocalizer: DateLocalizer,
    stateFactory: BusinessStateFactory,
    vmArgs: VmArgs
) : ViewModel(vmArgs) {

    private val shortWeekdayFormat = dateLocalizer.forStyle(DateStyle.NARROW_WEEKDAY)
    private val fullWeekdayFormat = dateLocalizer.forStyle(DateStyle.FULL_WEEKDAY)
    private val fullDateFormat = dateLocalizer.forStyle(DateStyle.D_MMM_YYYY_RELATIVE)

    val uiState: BusinessSettingsState = stateFactory.createBusinessSettingsState(createInitData()).setup()
    private var referenceBusiness: Business by Delegates.notNull()
    private var businessLocation: Business.Location? = null

    init {
        loadBusinessDetails()
    }

    private fun BusinessSettingsState.setup() = apply {
        appBar.onBackClick = weakVMClosure {
            it.uiState.navigation.push(BusinessSettingsDestination.Back)
        }
        setupDayOffs()
    }

    private fun loadBusinessDetails() {
        launch(
            launchIn = DispatcherProvider.io,
            call = { getBusinessById(businessId) },
            onComplete = {
                referenceBusiness = it
                businessLocation = it.location
                uiState.name.text = it.name
                uiState.description.text = it.description
                uiState.location.text = it.location?.toString().orEmpty()
                uiState.address.text = it.address
                uiState.currency.replaceOptions(Money.SupportedCurrency.entries.toCurrencyUI())
                uiState.currency.selectedItem = uiState.currency.options.first { currencyUI ->
                    currencyUI.domainValue == Money.SupportedCurrency.valueOf(it.currency.code())
                }.also { uiState.currency.textField.updateText(it.displayName) }
                uiState.currency.textField.placeholder = BusinessRes.strings.business_settings_currency_label.desc()
                uiState.instagram.text = it.socials[SocialKind.INSTAGRAM]?.value.orEmpty()
                uiState.telegram.text = it.socials[SocialKind.TELEGRAM]?.value.orEmpty()
                uiState.viber.text = it.socials[SocialKind.VIBER]?.value.orEmpty()
                uiState.phone.text = it.socials[SocialKind.PHONE]?.value.orEmpty()
                uiState.description.isValid = true
                uiState.location.isValid = true
                uiState.address.isValid = true
                uiState.instagram.isValid = true
                uiState.viber.isValid = true
                uiState.telegram.isValid = true
                uiState.name.isValid = true
                uiState.phone.isValid = true
                renderSchedule(it.schedule)
            },
            onError = { uiState.notifications.add(errorMapper.mapToNotification(it)) }
        )
    }

    fun onSaveClick() {
        val update = Business.Update(
            id = referenceBusiness.id,
            name = uiState.name.text.trim(),
            description = uiState.description.text.trim(),
            address = uiState.address.text.trim(),
            location = businessLocation,
            currency = Currency(uiState.currency.selectedItem!!.domainValue.name),
            socials = listOf(
                Social(SocialKind.PHONE, uiState.phone.text.trim()),
                Social(SocialKind.INSTAGRAM, uiState.instagram.text.trim()),
                Social(SocialKind.VIBER, uiState.viber.text.trim()),
                Social(SocialKind.TELEGRAM, uiState.telegram.text.trim())
            ).associateBy { it.kind },
            schedule = snapshotSchedule()
        )
        launch(
            launchIn = DispatcherProvider.io,
            onStart = { uiState.save.startLoading() },
            call = { updateBusiness(update) },
            onComplete = {
                referenceBusiness = it
                renderSchedule(it.schedule)
                uiState.notifications.add(GlobalMessage(BusinessRes.strings.business_settings_updated.desc()))
            },
            onError = {
                when (it) {
                    is UpdateBusiness.Error.ActiveDayWithoutWorkHours ->
                        uiState.notifications.add(
                            PresentationNotification.Message.simple(
                                BusinessRes.strings.business_settings_active_day_without_work_hours_error.desc()
                            )
                        )
                    is UpdateBusiness.Error.InvalidDayOffRange ->
                        uiState.notifications.add(
                            PresentationNotification.Message.simple(
                                BusinessRes.strings.business_settings_invalid_day_off_range_error.desc()
                            )
                        )
                    else -> uiState.notifications.add(errorMapper.mapToNotification(it))
                }
            },
            onTerminate = {
                uiState.save.stopLoading()
                invalidateSaveState()
            }
        )
    }

    fun onTestLocationClick() {
        businessLocation?.let {
            deviceFacade.openMapAt(it.lat, it.lng)
        }
    }

    fun onNameChanged(name: String) {
        val formatted = name.toOneLine()
        uiState.name.text = formatted
        uiState.name.isValid = formatted.isNotBlank()
        uiState.name.validationState = if (!uiState.name.isValid) ValidationState.ERROR else ValidationState.DEFAULT
        uiState.name.supportingTextRes = DesignSystem.strings.error_empty.desc().takeIf { !uiState.name.isValid }
        invalidateSaveState()
    }

    fun onDescriptionChanged(description: String) {
        uiState.description.text = description
        invalidateSaveState()
    }

    fun onLocationChanged(lat: Double, lng: Double) {
        uiState.location.text = "$lat, $lng"
        businessLocation = Business.Location(lat, lng)
        invalidateSaveState()
    }

    fun onAddressChanged(address: String) {
        uiState.address.text = address.toOneLine()
        invalidateSaveState()
    }

    fun onCurrencySelected(currencyUI: CurrencyUI) {
        uiState.currency.selectedItem = currencyUI
        uiState.currency.textField.updateText(currencyUI.displayName)
        invalidateSaveState()
    }

    fun onPhoneChanged(phone: String) {
        uiState.phone.text = phone.asPhone()
        invalidateSaveState()
    }

    fun onInstagramChanged(insta: String) {
        uiState.instagram.text = insta.toOneLine()
        invalidateSaveState()
    }

    fun onTelegramChanged(telegram: String) {
        uiState.telegram.text = telegram.toOneLine()
        invalidateSaveState()
    }

    fun onViberChanged(viber: String) {
        uiState.viber.text = viber.toOneLine()
        invalidateSaveState()
    }

    fun onPickLocationClicked() {

    }

    fun onAddPhotoClicked() {

    }

    /**
     * The working schedule and day offs belong to the business, so they are saved together with
     * the rest of the business details through [UpdateBusiness].
     */
    private fun snapshotSchedule(): WorkingSchedule = with(uiState.schedule) {
        WorkingSchedule(
            days = listOf(
                monday.toDomain(DayOfWeek.MONDAY),
                tuesday.toDomain(DayOfWeek.TUESDAY),
                wednesday.toDomain(DayOfWeek.WEDNESDAY),
                thursday.toDomain(DayOfWeek.THURSDAY),
                friday.toDomain(DayOfWeek.FRIDAY),
                saturday.toDomain(DayOfWeek.SATURDAY),
                sunday.toDomain(DayOfWeek.SUNDAY)
            ).associateBy { it.dayOfWeek },
            dayOffs = uiState.dayOffs.selectedItems.map { DayOffRange(it.dateFrom, it.dateTo) }
        )
    }

    private fun DaySettingsState.toDomain(dayOfWeek: DayOfWeek) = DayOfWeekSchedule(
        dayOfWeek = dayOfWeek,
        workingTime = intervals.mapNotNull { interval ->
            val from = interval.timeFromPicker.timePicker.pickedTime
            val to = interval.timeToPicker.timePicker.pickedTime
            if (from != null && to != null) WorkHour(from, to) else null
        },
        isActive = isActive.isChecked
    )

    private fun renderSchedule(workingSchedule: WorkingSchedule) = with(uiState) {
        dayOffs.replaceSelected(
            workingSchedule.dayOffs.map {
                DateRangePickerPresentation(
                    dateFrom = it.start,
                    dateTo = it.end,
                    formatter = fullDateFormat
                )
            }
        )
        dayOffs.onItemsRemoveRequested = weakVMClosure { vm, item ->
            vm.uiState.dayOffs.replaceSelected(vm.uiState.dayOffs.selectedItems.minus(item))
            vm.invalidateSaveState()
        }
        schedule.monday.render(workingSchedule.monday)
        schedule.tuesday.render(workingSchedule.tuesday)
        schedule.wednesday.render(workingSchedule.wednesday)
        schedule.thursday.render(workingSchedule.thursday)
        schedule.friday.render(workingSchedule.friday)
        schedule.saturday.render(workingSchedule.saturday)
        schedule.sunday.render(workingSchedule.sunday)

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
    }

    private fun DaySettingsState.render(daySchedule: DayOfWeekSchedule): DaySettingsState {
        val dayOfWeek = daySchedule.dayOfWeek
        id = dayOfWeek.hashCode().toString()
        isActive.isChecked = daySchedule.isActive
        isActive.onCheckedChange = weakVMClosure { vm, isChecked ->
            vm.uiState.schedule.dayOf(dayOfWeek).isActive.isChecked = isChecked
            vm.invalidateSaveState()
        }
        val weekday = LocalDate.atNextWeekDay(dayOfWeek)
        dayIndicator = shortWeekdayFormat.format(weekday).desc()
        title = fullWeekdayFormat.format(weekday).desc()
        addTimeButton.icon = DesignSystem.images.plus
        addTimeButton.text = BusinessRes.strings.business_settings_add_working_time.desc()
        addTimeButton.onClick = weakVMClosure { vm ->
            val day = vm.uiState.schedule.dayOf(dayOfWeek)
            day.replaceIntervals(day.intervals + day.createTimeSettingState().let { vm.renderInterval(it) })
            vm.invalidateSaveState()
        }
        onDeleteInterval = weakVMClosure { vm, interval ->
            val day = vm.uiState.schedule.dayOf(dayOfWeek)
            day.replaceIntervals(day.intervals - interval)
            vm.invalidateSaveState()
        }
        replaceIntervals(
            daySchedule.workingTime.map { time ->
                renderInterval(createTimeSettingState(), time)
            }
        )

        return this
    }

    private fun renderInterval(
        interval: TimeSettingState,
        hour: WorkHour? = null
    ): TimeSettingState = with(interval) {
        timeFromPicker.textField.placeholder = DesignSystem.strings.common_from.desc()
        timeToPicker.textField.placeholder = DesignSystem.strings.common_to.desc()

        timeFromPicker.textField.text = hour?.let { fullWeekdayFormat.format(it.from) }.orEmpty()
        timeToPicker.textField.text = hour?.let { fullWeekdayFormat.format(it.to) }.orEmpty()

        timeFromPicker.timePicker.pickedTime = hour?.from
        timeToPicker.timePicker.pickedTime = hour?.to

        val format = fullWeekdayFormat
        val notifyChanged = weakVMClosure { vm -> vm.invalidateSaveState() }

        val setFrom = weakSelfClosure { state: TimeSettingState, time: LocalTime ->
            state.timeFromPicker.textField.text = format.format(time)
            state.timeFromPicker.timePicker.pickedTime = time
        }
        timeFromPicker.timePicker.onTimePicked = { time ->
            setFrom(time)
            notifyChanged()
        }

        val setTo = weakSelfClosure { state: TimeSettingState, time: LocalTime ->
            state.timeToPicker.textField.text = format.format(time)
            state.timeToPicker.timePicker.pickedTime = time
        }
        timeToPicker.timePicker.onTimePicked = { time ->
            setTo(time)
            notifyChanged()
        }
        this
    }

    private fun BusinessSettingsState.setupDayOffs() {
        dayOffs.pickerTitle = BusinessRes.strings.business_settings_day_offs.desc()
        dayOffs.placeholder = BusinessRes.strings.business_settings_no_day_offs.desc()
        dayOffs.addItemButton.text = BusinessRes.strings.business_settings_add_day_off.desc()
        dayOffs.addItemButton.icon = DesignSystem.images.plus

        dateRange.title = BusinessRes.strings.business_settings_pick_day_off_title.desc()
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
        dateRange.onDateRangeSelected = weakVMClosure { vm ->
            val state = vm.uiState
            val startDate = state.dateRange.startDate.datePicker.pickedDate
            val endDate = state.dateRange.endDate.datePicker.pickedDate
            if (startDate != null && endDate != null) {
                val newItem = DateRangePickerPresentation(
                    dateFrom = startDate,
                    dateTo = endDate,
                    formatter = vm.fullDateFormat
                )
                state.dayOffs.replaceSelected(state.dayOffs.selectedItems + newItem)
                state.dateRange.startDate.datePicker.pickedDate = null
                state.dateRange.startDate.textField.text = ""
                state.dateRange.startDate.datePicker.maxDate = null
                state.dateRange.endDate.datePicker.pickedDate = null
                state.dateRange.endDate.textField.text = ""
                state.dateRange.endDate.datePicker.minDate = null
                vm.invalidateSaveState()
            }
        }
    }

    private fun ScheduleState.dayOf(dayOfWeek: DayOfWeek): DaySettingsState = when (dayOfWeek) {
        DayOfWeek.MONDAY -> monday
        DayOfWeek.TUESDAY -> tuesday
        DayOfWeek.WEDNESDAY -> wednesday
        DayOfWeek.THURSDAY -> thursday
        DayOfWeek.FRIDAY -> friday
        DayOfWeek.SATURDAY -> saturday
        else -> sunday
    }

    private fun invalidateSaveState() {
        val isAllFieldsValid = uiState.name.isValid &&
                uiState.description.isValid &&
                uiState.address.isValid &&
                uiState.location.isValid &&
                uiState.phone.isValid &&
                uiState.instagram.isValid &&
                uiState.telegram.isValid &&
                uiState.viber.isValid
        val isChanged = uiState.name.text != referenceBusiness.name ||
                uiState.description.text != referenceBusiness.description ||
                uiState.address.text != referenceBusiness.address ||
                uiState.currency.selectedItem?.domainValue?.code != referenceBusiness.currency.code() ||
                uiState.location.text != referenceBusiness.location?.toString().orEmpty() ||
                uiState.instagram.text != referenceBusiness.socials[SocialKind.INSTAGRAM]?.value ||
                uiState.telegram.text != referenceBusiness.socials[SocialKind.TELEGRAM]?.value ||
                uiState.viber.text != referenceBusiness.socials[SocialKind.VIBER]?.value ||
                uiState.phone.text != referenceBusiness.socials[SocialKind.PHONE]?.value ||
                isScheduleChanged()
        uiState.save.isEnabled = isAllFieldsValid && isChanged
    }

    private fun isScheduleChanged(): Boolean {
        val current = snapshotSchedule()
        val reference = referenceBusiness.schedule
        return current.days != reference.days || current.dayOffs != reference.dayOffs
    }

    companion object {
        internal fun createInitData() = BusinessSettingsState.InitData(
            title = BusinessRes.strings.business_settings_title.desc(),
            testLocationText = BusinessRes.strings.business_settings_location_test.desc(),
            saveButtonText = DesignSystem.strings.action_save.desc(),
            addPhotoText = BusinessRes.strings.business_settings_add_photo.desc(),
            nameHint = BusinessRes.strings.business_settings_name_hint.desc(),
            descriptionHint = BusinessRes.strings.business_settings_description_hint.desc(),
            addressHint = BusinessRes.strings.business_settings_address_hint.desc(),
            locationHint = BusinessRes.strings.business_settings_location_hint.desc(),
            phoneIcon = DesignSystem.images.phone,
            phoneHint = BusinessRes.strings.business_settings_phone_hint.desc(),
            instagramHint = BusinessRes.strings.business_settings_instagram_hint.desc(),
            viberHint = BusinessRes.strings.business_settings_viber_hint.desc(),
            telegramHint = BusinessRes.strings.business_settings_telegram_hint.desc(),
            telegramIcon = DesignSystem.images.telegram,
            viberIcon = DesignSystem.images.viber,
            instaIcon = DesignSystem.images.instagram,
            pickLocationText = BusinessRes.strings.business_settings_location_pick.desc()
        )
    }
}
