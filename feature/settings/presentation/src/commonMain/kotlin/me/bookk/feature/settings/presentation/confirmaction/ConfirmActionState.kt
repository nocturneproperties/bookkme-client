package me.bookk.feature.settings.presentation.confirmaction

import dev.icerock.moko.resources.ImageResource
import dev.icerock.moko.resources.desc.StringDesc
import me.bookk.designsystem.resources.color.ColorToken
import me.bookk.designsystem.uistate.AppBarState
import me.bookk.designsystem.uistate.ButtonState
import me.bookk.designsystem.uistate.NavigationState
import me.bookk.designsystem.uistate.PresentationNotificationState

interface ConfirmActionState {
    val appBar: AppBarState
    var icon: ImageResource?
    var iconColor: ColorToken
    var message: StringDesc
    var error: StringDesc?
    var isDestructive: Boolean
    val mainButton: ButtonState
    val notifications: PresentationNotificationState
    val navigation: NavigationState<ConfirmActionDestinations>
}
