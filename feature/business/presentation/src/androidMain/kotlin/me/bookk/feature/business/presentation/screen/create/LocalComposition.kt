package me.bookk.feature.business.presentation.screen.create

import androidx.compose.runtime.compositionLocalOf

internal class CreateBusinessEventListener(
    val onNameChanged: (String) -> Unit,
    val onCreateClick: () -> Unit,
)

internal val LocalCreateBusinessEventListener = compositionLocalOf {
    CreateBusinessEventListener(
        onNameChanged = {},
        onCreateClick = {}
    )
}