package me.bookk.feature.settings.presentation.confirmation

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import dev.icerock.moko.resources.desc.StringDesc

internal class AndroidChatConfirmationState : ChatConfirmationState {
    override var notice: StringDesc? by mutableStateOf(null)
    override var isWriteAccessRequested: Boolean by mutableStateOf(false)
    override var onWriteAccessAnswered: (Boolean) -> Unit = {}
}
