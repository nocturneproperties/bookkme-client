package me.bookk.feature.business.presentation.screen.settings

import dev.icerock.moko.resources.desc.desc
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flowOn
import library.device.api.DeviceFacade
import library.money.api.Currency
import library.money.api.Money
import me.bookk.android.designsystem.resources.DesignSystem
import me.bookk.android.feature.business.resources.BusinessRes
import me.bookk.core.coroutine.DispatcherProvider
import me.bookk.core.presentation.ViewModel
import me.bookk.core.presentation.VmArgs
import me.bookk.core.presentation.date.DateLocalizer
import me.bookk.core.presentation.error.ActionType
import me.bookk.core.presentation.error.ButtonDescriptor
import me.bookk.core.presentation.error.PresentationNotification
import me.bookk.core.presentation.error.PresentationNotification.GlobalMessage
import me.bookk.core.presentation.memory.weakVMClosure
import me.bookk.designsystem.resources.asPhone
import me.bookk.designsystem.resources.toOneLine
import me.bookk.designsystem.simple
import me.bookk.designsystem.uistate.InputType
import me.bookk.designsystem.uistate.ValidationState
import me.bookk.designsystem.uistate.schedule.DayOffPeriod
import me.bookk.designsystem.uistate.schedule.ScheduleBinder
import me.bookk.designsystem.uistate.schedule.WeekSchedule
import me.bookk.designsystem.uistate.schedule.WeekdaySchedule
import me.bookk.designsystem.uistate.schedule.WorkingHours
import me.bookk.designsystem.uistate.showError
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

class BusinessSettingsViewModel(
    private val deviceFacade: DeviceFacade,
    private val updateBusiness: UpdateBusiness,
    private val observeDashboardBusinessChanges: ObserveDashboardBusinessChanges,
    dateLocalizer: DateLocalizer,
    stateFactory: BusinessStateFactory,
    vmArgs: VmArgs,
) : ViewModel(vmArgs) {

    val uiState: BusinessSettingsState =
        stateFactory.createBusinessSettingsState(createInitData()).setup()
    private val scheduleBinder = ScheduleBinder(
        state = uiState.schedule,
        dateLocalizer = dateLocalizer,
        onChanged = weakVMClosure { it.invalidateSaveState() },
    )
    private var referenceBusiness: Business? = null
    private var businessLocation: Business.Location? = null

    init {
        observeCurrentBusiness()
    }

    private fun BusinessSettingsState.setup() = apply {
        save.isVisible = false
        appBar.onBackClick = weakVMClosure { it.onBackClick() }
        clientDataRetentionMonths.apply {
            suffix = BusinessRes.strings.business_settings_retention_months.desc()
            supportingTextRes = BusinessRes.strings.business_settings_retention_explanation.desc()
            inputType = InputType.DIGIT
            enabled = false
            onTextChanged = weakVMClosure { viewModel, text ->
                viewModel.onRetentionChanged(text)
            }
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
        uiState.apply {
            renderDetails(business)
            renderCurrency(business)
            renderSocials(business)
            clientDataRetentionMonths.text = business.clientDataRetentionMonths.toString()
        }
        validateRetention()
        renderSchedule(business.schedule)
        renderEditable(business.permissions.business.update)
        invalidateSaveState()
    }

    private fun BusinessSettingsState.renderDetails(business: Business) {
        name.apply {
            text = business.name
            isValid = true
        }
        description.apply {
            text = business.description
            isValid = true
        }
        location.apply {
            text = business.location?.toString().orEmpty()
            isValid = true
        }
        address.apply {
            text = business.address
            isValid = true
        }
    }

    private fun BusinessSettingsState.renderCurrency(business: Business) {
        currency.apply {
            replaceOptions(Money.SupportedCurrency.entries.toCurrencyUI())
            selectedItem = options
                .first {
                    it.domainValue == Money.SupportedCurrency.valueOf(business.currency.code())
                }
                .also { textField.updateText(it.displayName) }
            textField.placeholder = BusinessRes.strings.business_settings_currency_label.desc()
        }
    }

    private fun BusinessSettingsState.renderSocials(business: Business) {
        instagram.apply {
            text = business.socials[SocialKind.INSTAGRAM]?.value.orEmpty()
            isValid = true
        }
        telegram.apply {
            text = business.socials[SocialKind.TELEGRAM]?.value.orEmpty()
            isValid = true
        }
        viber.apply {
            text = business.socials[SocialKind.VIBER]?.value.orEmpty()
            isValid = true
        }
        phone.apply {
            text = business.socials[SocialKind.PHONE]?.value.orEmpty()
            isValid = true
        }
    }

    private fun renderEditable(isEditable: Boolean) = with(uiState) {
        name.enabled = isEditable
        description.enabled = isEditable
        address.enabled = isEditable
        phone.enabled = isEditable
        instagram.enabled = isEditable
        telegram.enabled = isEditable
        viber.enabled = isEditable
        currency.textField.enabled = isEditable
        clientDataRetentionMonths.enabled = isEditable
        photo.isEnabled = isEditable
        save.isVisible = isEditable
        scheduleBinder.isEditable = isEditable
    }

    private fun onBackClick() {
        if (hasUnsavedChanges()) {
            uiState.notifications.add(unsavedChangesConfirmation())
        } else {
            uiState.navigation.push(BusinessSettingsDestination.Back)
        }
    }

    private fun unsavedChangesConfirmation(): PresentationNotification.Message {
        return PresentationNotification.Message(
            title = BusinessRes.strings.business_settings_unsaved_changes_title.desc(),
            message = BusinessRes.strings.business_settings_unsaved_changes_message.desc(),
            buttons = listOf(
                ButtonDescriptor(
                    text = DesignSystem.strings.action_cancel.desc(),
                    actionType = ActionType.CANCEL,
                ),
                ButtonDescriptor(
                    text = BusinessRes.strings.business_settings_unsaved_changes_discard.desc(),
                    actionType = ActionType.NEGATIVE,
                    onClick = weakVMClosure {
                        it.uiState.navigation.push(BusinessSettingsDestination.Back)
                    },
                ),
            ),
        )
    }

    fun onSaveClick() {
        val reference = referenceBusiness ?: return
        if (uiState.save.isLoading || !uiState.clientDataRetentionMonths.isValid) return
        val update = Business.Update(
            id = reference.id,
            name = uiState.name.text.trim(),
            description = uiState.description.text.trim(),
            address = uiState.address.text.trim(),
            location = businessLocation,
            currency = Currency(uiState.currency.selectedItem!!.domainValue.name),
            socials = listOf(
                Social(SocialKind.PHONE, uiState.phone.text.trim()),
                Social(SocialKind.INSTAGRAM, uiState.instagram.text.trim()),
                Social(SocialKind.VIBER, uiState.viber.text.trim()),
                Social(SocialKind.TELEGRAM, uiState.telegram.text.trim()),
            )
                .associateBy { it.kind },
            schedule = snapshotSchedule(),
            clientDataRetentionMonths = uiState.clientDataRetentionMonths.text.toInt(),
        )
        launch(
            launchIn = DispatcherProvider.io,
            onStart = { uiState.save.startLoading() },
            call = { updateBusiness(update) },
            onComplete = {
                uiState.notifications.add(PresentationNotification.SuccessHaptic)
                referenceBusiness = it
                renderSchedule(it.schedule)
                uiState.notifications.add(
                    GlobalMessage(BusinessRes.strings.business_settings_updated.desc()),
                )
            },
            onError = {
                when (it) {
                    is UpdateBusiness.Error.InvalidRetentionPeriod -> {
                        uiState.clientDataRetentionMonths.showError(
                            BusinessRes.strings.business_settings_retention_error.desc(),
                        )
                    }

                    is UpdateBusiness.Error.CurrencyLocked -> {
                        uiState.notifications.add(
                            PresentationNotification.Message.simple(
                                BusinessRes.strings.business_settings_currency_locked_error.desc(),
                            ),
                        )
                    }

                    is UpdateBusiness.Error.ActiveDayWithoutWorkHours -> {
                        uiState.notifications.add(
                            PresentationNotification.Message.simple(
                                BusinessRes.strings
                                    .business_settings_active_day_without_work_hours_error
                                    .desc(),
                            ),
                        )
                    }

                    is UpdateBusiness.Error.InvalidDayOffRange -> {
                        uiState.notifications.add(
                            PresentationNotification.Message.simple(
                                BusinessRes.strings.business_settings_invalid_day_off_range_error
                                    .desc(),
                            ),
                        )
                    }

                    else -> {
                        uiState.notifications.add(errorMapper.mapToNotification(it))
                    }
                }
            },
            onTerminate = {
                uiState.save.stopLoading()
                invalidateSaveState()
            },
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
        uiState.name.validationState =
            if (!uiState.name.isValid) ValidationState.ERROR else ValidationState.DEFAULT
        uiState.name.supportingTextRes =
            DesignSystem.strings.error_empty.desc().takeIf { !uiState.name.isValid }
        invalidateSaveState()
    }

    fun onDescriptionChanged(description: String) {
        uiState.description.text = description
        invalidateSaveState()
    }

    private fun onRetentionChanged(text: String) {
        uiState.clientDataRetentionMonths.text = text
        validateRetention()
        invalidateSaveState()
    }

    private fun validateRetention() {
        val field = uiState.clientDataRetentionMonths
        val months = field.text.toIntOrNull()
        field.isValid = months != null && months in 12..120 && field.text.all { it.isDigit() }
        field.validationState =
            if (field.isValid) ValidationState.DEFAULT else ValidationState.ERROR
        field.supportingTextRes = if (field.isValid) {
            BusinessRes.strings.business_settings_retention_explanation.desc()
        } else {
            BusinessRes.strings.business_settings_retention_error.desc()
        }
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

    fun onAddPhotoClicked() {}

    private fun snapshotSchedule(): WorkingSchedule {
        return scheduleBinder.snapshot().toWorkingSchedule()
    }

    private fun renderSchedule(workingSchedule: WorkingSchedule) {
        scheduleBinder.render(workingSchedule.toWeekSchedule())
    }

    private fun WorkingSchedule.toWeekSchedule(): WeekSchedule {
        return WeekSchedule(
            days = listOf(
                monday,
                tuesday,
                wednesday,
                thursday,
                friday,
                saturday,
                sunday,
            )
                .map { day ->
                    WeekdaySchedule(
                        dayOfWeek = day.dayOfWeek,
                        isActive = day.isActive,
                        workingHours = day.workingTime.map { WorkingHours(it.from, it.to) },
                    )
                },
            dayOffs = dayOffs.map { DayOffPeriod(it.start, it.end) },
        )
    }

    private fun WeekSchedule.toWorkingSchedule(): WorkingSchedule {
        return WorkingSchedule(
            days = days.associate { day ->
                day.dayOfWeek to
                    DayOfWeekSchedule(
                        dayOfWeek = day.dayOfWeek,
                        workingTime = day.workingHours.map { WorkHour(it.from, it.to) },
                        isActive = day.isActive,
                    )
            },
            dayOffs = dayOffs.map { DayOffRange(it.start, it.end) },
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
            uiState.viber.isValid &&
            uiState.clientDataRetentionMonths.isValid
        uiState.save.isEnabled = !uiState.save.isLoading && isAllFieldsValid && hasUnsavedChanges()
    }

    private fun hasUnsavedChanges(): Boolean {
        val reference = referenceBusiness ?: return false
        return uiState.name.text != reference.name ||
            uiState.description.text != reference.description ||
            uiState.address.text != reference.address ||
            uiState.currency.selectedItem?.domainValue?.code != reference.currency.code() ||
            uiState.location.text != reference.location?.toString().orEmpty() ||
            uiState.instagram.text != reference.socials[SocialKind.INSTAGRAM]?.value.orEmpty() ||
            uiState.telegram.text != reference.socials[SocialKind.TELEGRAM]?.value.orEmpty() ||
            uiState.viber.text != reference.socials[SocialKind.VIBER]?.value.orEmpty() ||
            uiState.phone.text != reference.socials[SocialKind.PHONE]?.value.orEmpty() ||
            uiState.clientDataRetentionMonths.text.toIntOrNull() !=
            reference.clientDataRetentionMonths ||
            !uiState.clientDataRetentionMonths.isValid ||
            isScheduleChanged(reference.schedule)
    }

    private fun isScheduleChanged(reference: WorkingSchedule): Boolean {
        val current = snapshotSchedule()
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
        )
    }
}
