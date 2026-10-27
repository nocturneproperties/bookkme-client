package me.bookk.feature.settings.presentation.passkey

import androidx.compose.runtime.compositionLocalOf

internal class PasskeyEventListener(
    val onDeletePasskeyClick: (PasskeyState.PasskeyItem) -> Unit,
    val onAddPasskeyClick: () -> Unit,
    val onRefresh: () -> Unit
)

internal val LocalPasskeyEventListener = compositionLocalOf {
    PasskeyEventListener(
        onDeletePasskeyClick = {},
        onAddPasskeyClick = {},
        onRefresh = {}
    )
}