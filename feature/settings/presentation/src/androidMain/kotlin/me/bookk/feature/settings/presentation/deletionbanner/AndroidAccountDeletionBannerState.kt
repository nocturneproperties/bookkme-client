package me.bookk.feature.settings.presentation.deletionbanner

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import dev.icerock.moko.resources.desc.StringDesc
import dev.icerock.moko.resources.desc.desc
import me.bookk.designsystem.uistate.AndroidButtonState
import me.bookk.designsystem.uistate.AndroidNotificationState
import me.bookk.designsystem.uistate.ButtonState
import me.bookk.designsystem.uistate.PresentationNotificationState

internal class AndroidAccountDeletionBannerState : AccountDeletionBannerState {
    override var isVisible: Boolean by mutableStateOf(false)
    override var message: StringDesc by mutableStateOf("".desc())
    override val cancelButton: ButtonState = AndroidButtonState()
    override val notifications: PresentationNotificationState = AndroidNotificationState()
}
