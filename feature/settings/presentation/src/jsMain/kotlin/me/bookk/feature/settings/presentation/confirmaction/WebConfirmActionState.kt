package me.bookk.feature.settings.presentation.confirmaction

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import dev.icerock.moko.resources.ImageResource
import dev.icerock.moko.resources.desc.StringDesc
import dev.icerock.moko.resources.desc.desc
import me.bookk.designsystem.resources.color.ColorToken
import me.bookk.designsystem.uistate.WebAppBarState
import me.bookk.designsystem.uistate.WebButtonState
import me.bookk.designsystem.uistate.WebNavigationState
import me.bookk.designsystem.uistate.WebNotificationState

internal class WebConfirmActionState : ConfirmActionState {
    override val appBar = WebAppBarState()
    override var icon: ImageResource? by mutableStateOf(null)
    override var iconColor: ColorToken by mutableStateOf(ColorToken.ActionText)
    override var message: StringDesc by mutableStateOf("".desc())
    override var error: StringDesc? by mutableStateOf(null)
    override var isDestructive: Boolean by mutableStateOf(false)
    override val mainButton = WebButtonState()
    override val notifications = WebNotificationState()
    override val navigation = WebNavigationState<ConfirmActionDestinations>()
}
