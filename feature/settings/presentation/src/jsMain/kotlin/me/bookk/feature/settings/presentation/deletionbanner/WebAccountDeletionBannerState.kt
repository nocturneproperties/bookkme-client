package me.bookk.feature.settings.presentation.deletionbanner

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import dev.icerock.moko.resources.desc.StringDesc
import dev.icerock.moko.resources.desc.desc
import me.bookk.designsystem.uistate.WebButtonState
import me.bookk.designsystem.uistate.WebNotificationState

internal class WebAccountDeletionBannerState : AccountDeletionBannerState {
    override var isVisible: Boolean by mutableStateOf(false)
    override var message: StringDesc by mutableStateOf("".desc())
    override val cancelButton = WebButtonState()
    override val notifications = WebNotificationState()
}
