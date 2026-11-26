package me.bookk.feature.settings.presentation.accdelete

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import dev.icerock.moko.resources.desc.StringDesc
import dev.icerock.moko.resources.desc.desc
import me.bookk.designsystem.uistate.WebAppBarState
import me.bookk.designsystem.uistate.WebBooleanState
import me.bookk.designsystem.uistate.WebButtonState
import me.bookk.designsystem.uistate.WebNavigationState
import me.bookk.designsystem.uistate.WebNotificationState
import me.bookk.feature.settings.presentation.confirmation.WebChatConfirmationState

internal class WebDeleteAccountState : DeleteAccountState {
    override val appBar = WebAppBarState()
    override var confirmationMessage: StringDesc by mutableStateOf("".desc())
    override val confirmation = WebBooleanState()
    override val deleteButton = WebButtonState()
    override val chatConfirmation = WebChatConfirmationState()
    override val notifications = WebNotificationState()
    override val navigation = WebNavigationState<DeleteAccountNavigationDestination>()
}
