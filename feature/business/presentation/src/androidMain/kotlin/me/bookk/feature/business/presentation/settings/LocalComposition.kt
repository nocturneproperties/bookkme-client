package me.bookk.feature.business.presentation.settings

import androidx.compose.runtime.compositionLocalOf
import me.bookk.feature.business.presentation.settings.state.CurrencyUI

internal class BusinessSettingsEventListener(
    val onBackClick: () -> Unit,
    val onNameChanged: (String) -> Unit,
    val onDescriptionChanged: (String) -> Unit,
    val onAddressChanged: (String) -> Unit,
    val onCurrencySelected: (CurrencyUI) -> Unit,
    val onInstagramChanged: (String) -> Unit,
    val onTelegramChanged: (String) -> Unit,
    val onViberChanged: (String) -> Unit,
    val onTestLocationClick: () -> Unit,
    val onPickLocationClick: () -> Unit,
    val onSaveClick: () -> Unit,
)

internal val LocalBusinessSettingsEventListener = compositionLocalOf {
    BusinessSettingsEventListener(
        onNameChanged = {},
        onAddressChanged = {},
        onViberChanged = {},
        onDescriptionChanged = {},
        onTelegramChanged = {},
        onCurrencySelected = {},
        onInstagramChanged = {},
        onSaveClick = {},
        onTestLocationClick = {},
        onPickLocationClick = {},
        onBackClick = {}
    )
}