package me.bookk.feature.services.presentation.group.list

import me.bookk.designsystem.uistate.AppBarState
import me.bookk.designsystem.uistate.NavigationState
import me.bookk.designsystem.uistate.PresentationNotificationState

interface ServiceGroupListState {
    val appBar: AppBarState
    val notifications: PresentationNotificationState
    val navigation: NavigationState<ServiceGroupListDestination>
}
