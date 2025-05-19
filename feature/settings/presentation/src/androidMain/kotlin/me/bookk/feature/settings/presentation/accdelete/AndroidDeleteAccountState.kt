package me.bookk.feature.settings.presentation.accdelete

import dev.icerock.moko.resources.desc.StringDesc
import me.bookk.designsystem.uistate.AndroidAppBarState
import me.bookk.designsystem.uistate.AndroidButtonState
import me.bookk.designsystem.uistate.AndroidNavigationState
import me.bookk.designsystem.uistate.AndroidNotificationState
import me.bookk.designsystem.uistate.AndroidSwitchState
import me.bookk.designsystem.uistate.AppBarState
import me.bookk.designsystem.uistate.ButtonState
import me.bookk.designsystem.uistate.NavigationState
import me.bookk.designsystem.uistate.PresentationNotificationState
import me.bookk.designsystem.uistate.SwitchState

class AndroidDeleteAccountState(
    initData: DeleteAccountState.InitData
) : DeleteAccountState {
    override val appBar: AppBarState = AndroidAppBarState(initData.title)
    override val confirmationMessage: StringDesc = initData.confirmationMessage
    override val confirmationSwitch: SwitchState = AndroidSwitchState(initData.switchMessage, isChecked = false)
    override val deleteButton: ButtonState = AndroidButtonState(text = initData.buttonMessage, isEnabled = false)
    override val notifications: PresentationNotificationState = AndroidNotificationState()
    override val navigation: NavigationState<DeleteAccountNavigationDestination> = AndroidNavigationState()
}