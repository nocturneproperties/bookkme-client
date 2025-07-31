package me.bookk.feature.business.presentation.settings

import androidx.compose.runtime.Immutable
import dev.icerock.moko.resources.desc.desc
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
import me.bookk.feature.business.presentation.settings.state.BusinessSettingsState
import me.bookk.feature.business.presentation.settings.state.CurrencyUI

@Immutable
internal class AndroidBusinessSettingsState(initData: BusinessSettingsState.InitData) :
    BusinessSettingsState {
    override val appBar: AppBarState = AndroidAppBarState(initData.title)
    override val name: TextFieldState = AndroidTextFieldState(
        hint = initData.nameHint
    )
    override val description: TextFieldState = AndroidTextFieldState(
        hint = initData.descriptionHint
    )
    override val location: TextFieldState = AndroidTextFieldState(
        hint = initData.locationHint,
        supportingTextRes = initData.locationSupporting,
        readOnly = true
    )
    override val testLocation: ButtonState = AndroidButtonState(
        text = initData.testLocationText,
    )
    override val currency: PickerFieldState<CurrencyUI> = AndroidPickerFieldState(
        selectedItem = CurrencyUI("", "".desc()),
        items = listOf(CurrencyUI("", "".desc()))
    )
    override val address: TextFieldState = AndroidTextFieldState(
        hint = initData.addressHint
    )
    override val instagram: TextFieldState = AndroidTextFieldState(
        hint = initData.instagramHint
    )
    override val telegram: TextFieldState = AndroidTextFieldState(
        hint = initData.telegramHint
    )
    override val viber: TextFieldState = AndroidTextFieldState(
        hint = initData.viberHint
    )
    override val save: ButtonState = AndroidButtonState(
        text = initData.saveButtonText
    )

    override val notifications: PresentationNotificationState = AndroidNotificationState()
}