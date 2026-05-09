package me.bookk.feature.clients.presentation.details

import me.bookk.designsystem.uistate.AppBarState
import me.bookk.designsystem.uistate.ListState
import me.bookk.designsystem.uistate.NavigationState
import me.bookk.designsystem.uistate.PresentationNotificationState
import me.bookk.designsystem.uistate.simple.InfoLine

interface ClientDetailsState {
    val appBar: AppBarState
    val infoSections: ListState<InfoLine>

    val notifications: PresentationNotificationState
    val navigation: NavigationState<ClientDetailsDestination>
}