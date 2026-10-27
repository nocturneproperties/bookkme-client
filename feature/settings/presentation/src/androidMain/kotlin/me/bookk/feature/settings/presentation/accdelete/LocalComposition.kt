package me.bookk.feature.settings.presentation.accdelete

import androidx.compose.runtime.compositionLocalOf

internal class DeleteAccountEventListener(
    val onDeleteClick: () -> Unit,
    val onSwitchStateChanged: (Boolean) -> Unit
)

internal val LocalDeleteAccountEventListener = compositionLocalOf {
    DeleteAccountEventListener(
        onDeleteClick = {},
        onSwitchStateChanged = {}
    )
}