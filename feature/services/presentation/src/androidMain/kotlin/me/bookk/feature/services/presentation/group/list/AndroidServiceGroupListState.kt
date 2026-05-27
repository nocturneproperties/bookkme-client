package me.bookk.feature.services.presentation.group.list

import me.bookk.designsystem.uistate.AndroidAppBarState
import me.bookk.designsystem.uistate.AndroidNavigationState
import me.bookk.designsystem.uistate.AndroidNotificationState
import me.bookk.designsystem.uistate.AppBarState
import me.bookk.designsystem.uistate.NavigationState
import me.bookk.designsystem.uistate.PresentationNotificationState

internal class AndroidServiceGroupListState : ServiceGroupListState {
    override val appBar: AppBarState = AndroidAppBarState()
    override val notifications: PresentationNotificationState = AndroidNotificationState()
    override val navigation: NavigationState<ServiceGroupListDestination> = AndroidNavigationState()
}
