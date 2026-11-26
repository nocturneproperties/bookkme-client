package me.bookk.feature.settings.presentation.accdelete

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import dev.icerock.moko.resources.desc.StringDesc
import dev.icerock.moko.resources.desc.desc
import me.bookk.designsystem.uistate.AndroidAppBarState
import me.bookk.designsystem.uistate.AndroidBooleanState
import me.bookk.designsystem.uistate.AndroidButtonState
import me.bookk.designsystem.uistate.AndroidNavigationState
import me.bookk.designsystem.uistate.AndroidNotificationState
import me.bookk.designsystem.uistate.AppBarState
import me.bookk.designsystem.uistate.BooleanState
import me.bookk.designsystem.uistate.ButtonState
import me.bookk.designsystem.uistate.NavigationState
import me.bookk.designsystem.uistate.PresentationNotificationState
import me.bookk.feature.settings.presentation.confirmation.AndroidChatConfirmationState
import me.bookk.feature.settings.presentation.confirmation.ChatConfirmationState

class AndroidDeleteAccountState : DeleteAccountState {
    override val appBar: AppBarState = AndroidAppBarState()
    override var confirmationMessage: StringDesc by mutableStateOf("".desc())
    override val confirmation: BooleanState = AndroidBooleanState()
    override val deleteButton: ButtonState = AndroidButtonState()
    override val chatConfirmation: ChatConfirmationState = AndroidChatConfirmationState()
    override val notifications: PresentationNotificationState = AndroidNotificationState()
    override val navigation: NavigationState<DeleteAccountNavigationDestination> =
        AndroidNavigationState()
}
