package me.bookk.feature.clients.presentation.select

import me.bookk.designsystem.uistate.AndroidAppBarState
import me.bookk.designsystem.uistate.AndroidListState
import me.bookk.designsystem.uistate.AndroidNavigationState
import me.bookk.designsystem.uistate.AndroidNotificationState
import me.bookk.designsystem.uistate.AndroidRefreshState
import me.bookk.designsystem.uistate.AndroidTextFieldState
import me.bookk.designsystem.uistate.AppBarState
import me.bookk.designsystem.uistate.ListState
import me.bookk.designsystem.uistate.NavigationState
import me.bookk.designsystem.uistate.PresentationNotificationState
import me.bookk.designsystem.uistate.RefreshState
import me.bookk.designsystem.uistate.TextFieldState
import me.bookk.feature.clients.presentation.list.ClientListItem

internal class AndroidSelectClientState : SelectClientState {
    override val appBar: AppBarState = AndroidAppBarState()
    override val searchField: TextFieldState = AndroidTextFieldState()
    override val clients: ListState<ClientListItem> = AndroidListState()
    override val refreshState: RefreshState = AndroidRefreshState()
    override val notifications: PresentationNotificationState = AndroidNotificationState()
    override val navigation: NavigationState<SelectClientDestination> = AndroidNavigationState()
}
