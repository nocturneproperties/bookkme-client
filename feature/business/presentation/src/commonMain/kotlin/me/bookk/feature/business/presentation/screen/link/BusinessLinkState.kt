package me.bookk.feature.business.presentation.screen.link

import dev.icerock.moko.resources.desc.StringDesc
import me.bookk.designsystem.uistate.AppBarState
import me.bookk.designsystem.uistate.ButtonState
import me.bookk.designsystem.uistate.NavigationState
import me.bookk.designsystem.uistate.PresentationNotificationState
import me.bookk.designsystem.uistate.RefreshState
import me.bookk.designsystem.uistate.TextFieldState

interface BusinessLinkState {
    val appBar: AppBarState
    val name: TextFieldState
    var preview: StringDesc
    var loadError: StringDesc?
    val refresh: RefreshState
    val save: ButtonState
    val share: ButtonState
    val notifications: PresentationNotificationState
    val navigation: NavigationState<BusinessLinkDestinations>
}
