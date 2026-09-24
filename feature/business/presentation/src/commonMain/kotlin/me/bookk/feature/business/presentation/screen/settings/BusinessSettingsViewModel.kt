package me.bookk.feature.business.presentation.screen.settings

import dev.icerock.moko.resources.desc.desc
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flowOn
import library.device.api.DeviceFacade
import library.money.api.Currency
import library.money.api.Money
import me.bookk.android.feature.business.resources.BusinessRes
import me.bookk.core.coroutine.DispatcherProvider
import me.bookk.core.presentation.ViewModel
import me.bookk.core.presentation.VmArgs
import me.bookk.core.presentation.date.DateLocalizer
import me.bookk.core.presentation.error.PresentationNotification
import me.bookk.core.presentation.error.PresentationNotification.GlobalMessage
import me.bookk.core.presentation.memory.weakVMClosure
import me.bookk.designsystem.resources.DesignSystem
import me.bookk.designsystem.resources.asPhone
import me.bookk.designsystem.resources.toOneLine
import me.bookk.designsystem.simple
import me.bookk.designsystem.uistate.ValidationState
import me.bookk.designsystem.uistate.schedule.DayOffPeriod
import me.bookk.designsystem.uistate.schedule.ScheduleBinder
import me.bookk.designsystem.uistate.schedule.WeekSchedule
import me.bookk.designsystem.uistate.schedule.WeekdaySchedule
import me.bookk.designsystem.uistate.schedule.WorkingHours
import me.bookk.designsystem.uistate.startLoading
import me.bookk.designsystem.uistate.stopLoading
import me.bookk.feature.business.domain.api.business.ObserveDashboardBusinessChanges
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
import me.bookk.feature.business.presentation.screen.settings.state.toCurrencyUI
import kotlin.properties.Delegates

class BusinessSettingsViewModel(
    private val deviceFacade: DeviceFacade,
    private val updateBusiness: UpdateBusiness,
    private val observeDashboardBusinessChanges: ObserveDashboardBusinessChanges,
    dateLocalizer: DateLocalizer,
    stateFactory: BusinessStateFactory,
    vmArgs: VmArgs
) : ViewModel(vmArgs) {

    val uiState: BusinessSettingsState = stateFactory.createBusinessSettingsState(createInitData()).setup()
    private val scheduleBinder = ScheduleBinder(uiState.schedule, dateLocalizer, weakVMClosure { it.invalidateSaveState() })
    private var referenceBusiness: Business by Delegates.notNull()
    private var businessLocation: Business.Location? = null

    init {
        observeCurrentBusiness()
    }

    private fun BusinessSettingsState.setup() = apply {
        appBar.onBackClick = weakVMClosure {
            it.uiState.navigation.push(BusinessSettingsDestination.Back)
        }
    }

    private fun observeCurrentBusiness() {
        observeDashboardBusinessChanges()
            .flowOn(DispatcherProvider.io)
            .filterNotNull()
            .distinctUntilChanged()
            .safeOnEach { renderBusiness(it) }
            .onError { uiState.notifications.add(it.notification()) }
            .observe()
    }

    private fun renderBusiness(business: Business) {
        referenceBusiness = business
        businessLocation = business.location
        uiState.name.text = business.name
        uiState.description.text = business.description
        uiState.location.text = business.location?.toString().orEmpty()
        uiState.address.text = business.address
        uiState.currency.replaceOptions(Money.SupportedCurrency.entries.toCurrencyUI())
        uiState.currency.selectedItem = uiState.currency.options.first { currencyUI ->
            currencyUI.domainValue == Money.SupportedCurrency.valueOf(business.currency.code())
        }.also { uiState.currency.textField.updateText(it.displayName) }
        uiState.currency.textField.placeholder = BusinessRes.strings.business_settings_currency_label.desc()
        uiState.instagram.text = business.socials[SocialKind.INSTAGRAM]?.value.orEmpty()
        uiState.telegram.text = business.socials[SocialKind.TELEGRAM]?.value.orEmpty()
        uiState.viber.text = business.socials[SocialKind.VIBER]?.value.orEmpty()
        uiState.phone.text = business.socials[SocialKind.PHONE]?.value.orEmpty()
        uiState.description.isValid = true
        uiState.location.isValid = true
        uiState.address.isValid = true
        uiState.instagram.isValid = true
        uiState.viber.isValid = true
        uiState.telegram.isValid = true
        uiState.name.isValid = true
        uiState.phone.isValid = true
        renderSchedule(business.schedule)
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

    private fun snapshotSchedule(): WorkingSchedule {
        return scheduleBinder.snapshot().toWorkingSchedule()
    }

    private fun renderSchedule(workingSchedule: WorkingSchedule) {
        scheduleBinder.render(workingSchedule.toWeekSchedule())
    }

    private fun WorkingSchedule.toWeekSchedule(): WeekSchedule {
        return WeekSchedule(
            days = listOf(monday, tuesday, wednesday, thursday, friday, saturday, sunday).map { day ->
                WeekdaySchedule(
                    dayOfWeek = day.dayOfWeek,
                    isActive = day.isActive,
                    workingHours = day.workingTime.map { WorkingHours(it.from, it.to) }
                )
            },
            dayOffs = dayOffs.map { DayOffPeriod(it.start, it.end) }
        )
    }

    private fun WeekSchedule.toWorkingSchedule(): WorkingSchedule {
        return WorkingSchedule(
            days = days.associate { day ->
                day.dayOfWeek to DayOfWeekSchedule(
                    dayOfWeek = day.dayOfWeek,
                    workingTime = day.workingHours.map { WorkHour(it.from, it.to) },
                    isActive = day.isActive
                )
            },
            dayOffs = dayOffs.map { DayOffRange(it.start, it.end) }
        )
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
                uiState.instagram.text != referenceBusiness.socials[SocialKind.INSTAGRAM]?.value.orEmpty() ||
                uiState.telegram.text != referenceBusiness.socials[SocialKind.TELEGRAM]?.value.orEmpty() ||
                uiState.viber.text != referenceBusiness.socials[SocialKind.VIBER]?.value.orEmpty() ||
                uiState.phone.text != referenceBusiness.socials[SocialKind.PHONE]?.value.orEmpty() ||
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
