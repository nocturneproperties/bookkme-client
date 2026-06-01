package me.bookk.feature.services.presentation.group.add

import dev.icerock.moko.resources.desc.StringDesc
import me.bookk.designsystem.uistate.ButtonState
import me.bookk.designsystem.uistate.NavigationState
import me.bookk.designsystem.uistate.PresentationNotificationState
import me.bookk.designsystem.uistate.TextFieldState

interface AddGroupState {
    var title: StringDesc
    val name: TextFieldState
    val create: ButtonState

    val notifications: PresentationNotificationState
    val navigation: NavigationState<AddGroupNavigation>
}