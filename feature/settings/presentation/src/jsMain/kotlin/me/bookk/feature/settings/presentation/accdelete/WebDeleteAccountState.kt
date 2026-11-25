package me.bookk.feature.settings.presentation.accdelete

import dev.icerock.moko.resources.desc.StringDesc
import me.bookk.designsystem.uistate.WebAppBarState
import me.bookk.designsystem.uistate.WebBooleanState
import me.bookk.designsystem.uistate.WebButtonState
import me.bookk.designsystem.uistate.WebNavigationState
import me.bookk.designsystem.uistate.WebNotificationState

internal class WebDeleteAccountState(
    initData: DeleteAccountState.InitData,
) : DeleteAccountState {
    override val appBar = WebAppBarState(title = initData.title)
    override val confirmationMessage: StringDesc = initData.confirmationMessage
    override val confirmation = WebBooleanState(text = initData.switchMessage)
    override val deleteButton = WebButtonState(text = initData.buttonMessage, isEnabled = false)
    override val notifications = WebNotificationState()
    override val navigation = WebNavigationState<DeleteAccountNavigationDestination>()
}
