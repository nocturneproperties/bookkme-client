package me.bookk.feature.business.presentation.settings

import dev.icerock.moko.resources.desc.desc
import library.device.api.DeviceFacade
import me.bookk.android.feature.business.resources.BusinessRes
import me.bookk.core.presentation.ViewModel
import me.bookk.core.presentation.VmArgs
import me.bookk.designsystem.resources.DesignSystem
import me.bookk.feature.business.presentation.BusinessStateFactory
import me.bookk.feature.business.presentation.settings.state.BusinessSettingsState
import me.bookk.feature.business.presentation.settings.state.CurrencyUI

class BusinessSettingsViewModel(
    private val deviceFacade: DeviceFacade,
    stateFactory: BusinessStateFactory,
    vmArgs: VmArgs
) : ViewModel(vmArgs) {

    val uiState: BusinessSettingsState = stateFactory.createBusinessSettingsState(createInitData())

    fun onSaveClick() {

    }

    fun onTestLocationClick() {

    }

    fun onNameChanged(name: String) {
        uiState.name.text = name
        uiState.name.isValid = name.isNotBlank()
        uiState.name.isError = !uiState.name.isValid
        uiState.name.supportingTextRes = DesignSystem.strings.error_empty.desc()
    }

    fun onDescriptionChanged(description: String) {
        uiState.description.text = description
    }

    fun onLocationChanged(lat: Double, lng: Double) {
        uiState.location.text = "$lat, $lng"
    }

    fun onAddressChanged(address: String) {
        uiState.address.text = address
    }

    fun onCurrencySelected(currencyUI: CurrencyUI) {
        uiState.currency.selectedItem = currencyUI
    }

    fun onInstagramChanged(insta: String) {
        uiState.instagram.text = insta
    }

    fun onTelegramChanged(telegram: String) {
        uiState.telegram.text = telegram
    }

    fun onViberChanged(viber: String) {
        uiState.viber.text = viber
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
            locationSupporting = BusinessRes.strings.business_settings_location_supporting.desc(),
            instagramHint = BusinessRes.strings.business_settings_instagram_hint.desc(),
            viberHint = BusinessRes.strings.business_settings_viber_hint.desc(),
            telegramHint = BusinessRes.strings.business_settings_telegram_hint.desc(),
            telegramIcon = DesignSystem.images.telegram,
            viberIcon = DesignSystem.images.viber,
            instaIcon = DesignSystem.images.instagram
        )
    }
}