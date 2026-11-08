package me.bookk.feature.settings.presentation.editprofile

import androidx.compose.runtime.compositionLocalOf

internal class EditProfileEventListener(
    val onNameChanged: (String) -> Unit,
    val onLastNameChanged: (String) -> Unit,
    val onSaveClick: () -> Unit,
)

internal val LocalEditProfileEventListener = compositionLocalOf {
    EditProfileEventListener(
        onNameChanged = {},
        onLastNameChanged = {},
        onSaveClick = {},
    )
}
