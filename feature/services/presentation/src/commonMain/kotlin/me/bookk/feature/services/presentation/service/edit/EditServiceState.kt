package me.bookk.feature.services.presentation.service.edit

import me.bookk.designsystem.uistate.AppBarState
import me.bookk.designsystem.uistate.NavigationState
import me.bookk.designsystem.uistate.PresentationNotificationState

interface EditServiceState {
    val appBar: AppBarState

    val notifications: PresentationNotificationState
    val navigation: NavigationState<EditServiceDestination>
}
