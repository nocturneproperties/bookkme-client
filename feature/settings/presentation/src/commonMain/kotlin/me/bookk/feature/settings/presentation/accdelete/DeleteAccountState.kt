package me.bookk.feature.settings.presentation.accdelete

import dev.icerock.moko.resources.desc.StringDesc
import me.bookk.designsystem.uistate.AppBarState
import me.bookk.designsystem.uistate.BooleanState
import me.bookk.designsystem.uistate.ButtonState
import me.bookk.designsystem.uistate.NavigationState
import me.bookk.designsystem.uistate.PresentationNotificationState

interface DeleteAccountState {
    val appBar: AppBarState
    var confirmationMessage: StringDesc
    val confirmation: BooleanState
    val deleteButton: ButtonState

    val notifications: PresentationNotificationState
    val navigation: NavigationState<DeleteAccountNavigationDestination>
}
