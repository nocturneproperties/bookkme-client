package me.bookk.feature.business.presentation.clients.list

import me.bookk.designsystem.uistate.AppBarState
import me.bookk.designsystem.uistate.ListState
import me.bookk.designsystem.uistate.NavigationState
import me.bookk.designsystem.uistate.PresentationNotificationState
import me.bookk.designsystem.uistate.RefreshState
import me.bookk.designsystem.uistate.TextFieldState

interface ClientsListState {
    val appBar: AppBarState
    val searchField: TextFieldState
    val clientsList: ListState<ClientSection>
    val refreshState: RefreshState

    val notifications: PresentationNotificationState
    val navigation: NavigationState<ClientsListDestination>
}