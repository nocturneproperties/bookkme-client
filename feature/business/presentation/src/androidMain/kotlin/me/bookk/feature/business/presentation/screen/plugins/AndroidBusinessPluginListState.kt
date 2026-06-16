package me.bookk.feature.business.presentation.screen.plugins

import me.bookk.designsystem.uistate.AndroidAppBarState
import me.bookk.designsystem.uistate.AndroidNavigationState
import me.bookk.designsystem.uistate.AndroidNotificationState
import me.bookk.designsystem.uistate.AppBarState
import me.bookk.designsystem.uistate.NavigationState
import me.bookk.designsystem.uistate.PresentationNotificationState

internal class AndroidBusinessPluginListState : BusinessPluginListState {
    override val appBar: AppBarState = AndroidAppBarState()
    override val appointmentPlugin: BusinessPluginState = AndroidBusinessPluginState()

    override val notifications: PresentationNotificationState = AndroidNotificationState()
    override val navigation: NavigationState<BusinessPluginsDestinations> = AndroidNavigationState()
}