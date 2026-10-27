package me.bookk.feature.settings.presentation.accdelete

import dev.icerock.moko.resources.desc.StringDesc
import me.bookk.designsystem.uistate.AppBarState
import me.bookk.designsystem.uistate.BooleanState
import me.bookk.designsystem.uistate.ButtonState
import me.bookk.designsystem.uistate.NavigationState
import me.bookk.designsystem.uistate.PresentationNotificationState

interface DeleteAccountState {
    val appBar: AppBarState
    val confirmationMessage: StringDesc
    val confirmation: BooleanState
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