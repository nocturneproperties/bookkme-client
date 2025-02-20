package me.bookk.feature.settings.presentation.contactus

import androidx.compose.runtime.compositionLocalOf

internal class ContactUsEventListener(
    val onSubmitClick: () -> Unit,
    val onTextChanged: (String) -> Unit,
    val onIncludeUsageLogsCheckedChanged: (Boolean) -> Unit
)

internal val LocalContactUsEventListener = compositionLocalOf {
    ContactUsEventListener(
        onSubmitClick = {},
        onTextChanged = {},
        onIncludeUsageLogsCheckedChanged = {}
    )
}