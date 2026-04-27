package me.bookk.feature.business.presentation.settings

import androidx.compose.runtime.Immutable
import dev.icerock.moko.resources.desc.desc
import library.money.api.Money
import me.bookk.designsystem.uistate.AndroidAppBarState
import me.bookk.designsystem.uistate.AndroidButtonState
import me.bookk.designsystem.uistate.AndroidNotificationState
import me.bookk.designsystem.uistate.AndroidPickerFieldState
import me.bookk.designsystem.uistate.AndroidTextFieldState
import me.bookk.designsystem.uistate.AppBarState
import me.bookk.designsystem.uistate.ButtonState
import me.bookk.designsystem.uistate.PickerFieldState
import me.bookk.designsystem.uistate.PresentationNotificationState
import me.bookk.designsystem.uistate.TextFieldState
import me.bookk.designsystem.uistate.TopBarSize
import me.bookk.feature.business.presentation.settings.state.BusinessSettingsState
import me.bookk.feature.business.presentation.settings.state.CurrencyUI

@Immutable
internal class AndroidBusinessSettingsState(initData: BusinessSettingsState.InitData) :
    BusinessSettingsState {
    override val appBar: AppBarState = AndroidAppBarState(initData.title, size = TopBarSize.SMALL)
    override val name: TextFieldState = AndroidTextFieldState(
        label = initData.nameHint
    )
    override val description: TextFieldState = AndroidTextFieldState(
        label = initData.descriptionHint
    )
    override val location: TextFieldState = AndroidTextFieldState(
        label = initData.locationHint,
        readOnly = true
    )
    override val testLocation: ButtonState = AndroidButtonState(
        text = initData.testLocationText,
    )
    override val currency: PickerFieldState<CurrencyUI> = AndroidPickerFieldState(
        textFieldState = AndroidTextFieldState(),
        selectedItem = CurrencyUI("0", "".desc(), Money.SupportedCurrency.EUR),
        items = listOf(CurrencyUI("0", "".desc(), Money.SupportedCurrency.EUR))
    )
    override val address: TextFieldState = AndroidTextFieldState(
        label = initData.addressHint
    )
    override val phone: TextFieldState = AndroidTextFieldState(
        startIcon = initData.phoneIcon,
        label = initData.phoneHint
    )
    override val instagram: TextFieldState = AndroidTextFieldState(
        startIcon = initData.instaIcon,
        label = initData.instagramHint
    )
    override val telegram: TextFieldState = AndroidTextFieldState(
        startIcon = initData.telegramIcon,
        label = initData.telegramHint
    )
    override val viber: TextFieldState = AndroidTextFieldState(
        startIcon = initData.viberIcon,
        label = initData.viberHint
    )
    override val save: ButtonState = AndroidButtonState(
        text = initData.saveButtonText,
        isEnabled = false
    )
    override val pickLocation: ButtonState = AndroidButtonState(
        text = initData.pickLocationText
    )

    override val notifications: PresentationNotificationState = AndroidNotificationState()
}