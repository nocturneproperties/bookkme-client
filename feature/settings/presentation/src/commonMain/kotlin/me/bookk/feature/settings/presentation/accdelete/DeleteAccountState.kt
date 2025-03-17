package me.bookk.feature.settings.presentation.accdelete

import dev.icerock.moko.resources.desc.StringDesc
import me.bookk.designsystem.uistate.AppBarState
import me.bookk.designsystem.uistate.ButtonState
import me.bookk.designsystem.uistate.NavigationState
import me.bookk.designsystem.uistate.PresentationNotificationState
import me.bookk.designsystem.uistate.SwitchState

interface DeleteAccountState {
    val appBar: AppBarState
    val confirmationMessage: StringDesc
    val confirmationSwitch: SwitchState
    val deleteButton: ButtonState

    val notifications: PresentationNotificationState
    val navigation: NavigationState<DeleteAccountNavigationDestination>

    class InitData(
        val title: StringDesc,
        val confirmationMessage: StringDesc,
        val switchMessage: StringDesc,
        val buttonMessage: StringDesc
    )
}