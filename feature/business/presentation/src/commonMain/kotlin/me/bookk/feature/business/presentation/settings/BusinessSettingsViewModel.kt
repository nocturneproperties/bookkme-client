package me.bookk.feature.business.presentation.settings

import dev.icerock.moko.resources.desc.desc
import library.device.api.DeviceFacade
import library.money.api.CurrencyFactory
import library.money.api.Money
import me.bookk.android.feature.business.resources.BusinessRes
import me.bookk.core.LogFactory
import me.bookk.core.coroutine.DispatcherProvider
import me.bookk.core.presentation.ViewModel
import me.bookk.core.presentation.VmArgs
import me.bookk.core.presentation.error.PresentationNotification.GlobalMessage
import me.bookk.designsystem.resources.DesignSystem
import me.bookk.designsystem.resources.asPhone
import me.bookk.designsystem.resources.toOneLine
import me.bookk.designsystem.uistate.ValidationState
import me.bookk.designsystem.uistate.startLoading
import me.bookk.designsystem.uistate.stopLoading
import me.bookk.feature.business.domain.api.GetBusinessById
import me.bookk.feature.business.domain.api.UpdateBusiness
import me.bookk.feature.business.domain.api.entity.Business
import me.bookk.feature.business.domain.api.entity.Business.Social
import me.bookk.feature.business.domain.api.entity.Business.SocialKind
import me.bookk.feature.business.presentation.BusinessStateFactory
import me.bookk.feature.business.presentation.settings.state.BusinessSettingsState
import me.bookk.feature.business.presentation.settings.state.CurrencyUI
import me.bookk.feature.business.presentation.settings.state.toCurrencyUI
import kotlin.properties.Delegates
import kotlin.uuid.Uuid

class BusinessSettingsViewModel(
    private val businessId: Uuid,
    private val deviceFacade: DeviceFacade,
    private val getBusinessById: GetBusinessById,
    private val updateBusiness: UpdateBusiness,
    stateFactory: BusinessStateFactory,
    vmArgs: VmArgs
) : ViewModel(vmArgs) {

    val uiState: BusinessSettingsState = stateFactory.createBusinessSettingsState(createInitData())
    private var referenceBusiness: Business by Delegates.notNull()
    private var businessLocation: Business.Location? = null
    private val logger = LogFactory.createLogger("BusinessSettingsViewModel")

    init {
        loadBusinessDetails()
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
            },
            onError = { uiState.notifications.add(errorMapper.mapToNotification(it)) }
        )
    }

    fun onSaveClick() {
        launch(
            launchIn = DispatcherProvider.io,
            onStart = { uiState.save.startLoading() },
            call = {
                updateBusiness(
                    Business.Update(
                        id = referenceBusiness.id,
                        name = uiState.name.text.trim(),
                        description = uiState.description.text.trim(),
                        address = uiState.address.text.trim(),
                        location = businessLocation,
                        currency = CurrencyFactory.forCode(uiState.currency.selectedItem!!.domainValue.name),
                        socials = listOf(
                            Social(SocialKind.PHONE, uiState.phone.text.trim()),
                            Social(SocialKind.INSTAGRAM, uiState.instagram.text.trim()),
                            Social(SocialKind.VIBER, uiState.viber.text.trim()),
                            Social(SocialKind.TELEGRAM, uiState.telegram.text.trim())
                        ).associateBy { it.kind }
                    )
                )
            },
            onComplete = {
                referenceBusiness = it
                uiState.notifications.add(GlobalMessage(BusinessRes.strings.business_settings_updated.desc()))
            },
            onError = { uiState.notifications.add(errorMapper.mapToNotification(it)) },
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
                uiState.phone.text != referenceBusiness.socials[SocialKind.PHONE]?.value
        uiState.save.isEnabled = isAllFieldsValid && isChanged
    }

    companion object {
        internal fun createInitData() = BusinessSettingsState.InitData(
            title = BusinessRes.strings.business_settings_title.desc(),
            testLocationText = BusinessRes.strings.business_settings_location_test.desc(),
            saveButtonText = DesignSystem.strings.action_save.desc(),
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