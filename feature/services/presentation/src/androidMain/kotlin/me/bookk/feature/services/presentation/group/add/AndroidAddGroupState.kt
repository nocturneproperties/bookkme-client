package me.bookk.feature.services.presentation.group.add

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import dev.icerock.moko.resources.desc.StringDesc
import dev.icerock.moko.resources.desc.desc
import me.bookk.designsystem.uistate.AndroidButtonState
import me.bookk.designsystem.uistate.AndroidNavigationState
import me.bookk.designsystem.uistate.AndroidNotificationState
import me.bookk.designsystem.uistate.AndroidTextFieldState
import me.bookk.designsystem.uistate.ButtonState
import me.bookk.designsystem.uistate.NavigationState
import me.bookk.designsystem.uistate.PresentationNotificationState
import me.bookk.designsystem.uistate.TextFieldState

internal class AndroidAddGroupState : AddGroupState {
    override var title: StringDesc by mutableStateOf("".desc())
    override val name: TextFieldState = AndroidTextFieldState()
    override val create: ButtonState = AndroidButtonState()
    override val notifications: PresentationNotificationState = AndroidNotificationState()
    override val navigation: NavigationState<AddGroupNavigation> = AndroidNavigationState()
}