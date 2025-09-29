package me.bookk.feature.business.presentation.settings.state

import dev.icerock.moko.resources.ImageResource
import dev.icerock.moko.resources.desc.StringDesc
import me.bookk.designsystem.uistate.AppBarState
import me.bookk.designsystem.uistate.ButtonState
import me.bookk.designsystem.uistate.PickerFieldState
import me.bookk.designsystem.uistate.PresentationNotificationState
import me.bookk.designsystem.uistate.TextFieldState

interface BusinessSettingsState {
    val appBar: AppBarState
    val name: TextFieldState
    val description: TextFieldState
    val location: TextFieldState
    val address: TextFieldState
    val currency: PickerFieldState<CurrencyUI>
    val instagram: TextFieldState
    val telegram: TextFieldState
    val viber: TextFieldState

    val pickLocation: ButtonState
    val testLocation: ButtonState
    val save: ButtonState

    val notifications: PresentationNotificationState

    class InitData(
        val title: StringDesc,
        val testLocationText: StringDesc,
        val saveButtonText: StringDesc,
        val nameHint: StringDesc,
        val descriptionHint: StringDesc,
        val addressHint: StringDesc,
        val locationHint: StringDesc,
        val locationSupporting: StringDesc,
        val instagramHint: StringDesc,
        val viberHint: StringDesc,
        val telegramHint: StringDesc,
        val instaIcon: ImageResource,
        val viberIcon: ImageResource,
        val telegramIcon: ImageResource,
        val pickLocationText: StringDesc
    )
}