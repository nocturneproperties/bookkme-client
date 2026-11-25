package me.bookk.feature.clients.presentation.select

import me.bookk.designsystem.uistate.AppBarState
import me.bookk.designsystem.uistate.ListState
import me.bookk.designsystem.uistate.NavigationState
import me.bookk.designsystem.uistate.PresentationNotificationState
import me.bookk.designsystem.uistate.RefreshState
import me.bookk.designsystem.uistate.TextFieldState
import me.bookk.feature.clients.presentation.list.ClientListItem

interface SelectClientState {
    val appBar: AppBarState
    val searchField: TextFieldState
    val clients: ListState<ClientListItem>
    val refreshState: RefreshState

    val notifications: PresentationNotificationState
    val navigation: NavigationState<SelectClientDestination>
}
