package me.bookk.feature.appointments.presentation.screen.details

import me.bookk.designsystem.uistate.AppBarState
import me.bookk.designsystem.uistate.ListState
import me.bookk.designsystem.uistate.NavigationState
import me.bookk.designsystem.uistate.PresentationNotificationState
import me.bookk.designsystem.uistate.simple.InfoLine

interface AppointmentDetailsState {
    val appBar: AppBarState
    val infoSections: ListState<InfoLine>

    val notifications: PresentationNotificationState
    val navigation: NavigationState<AppointmentDetailsDestination>
}
