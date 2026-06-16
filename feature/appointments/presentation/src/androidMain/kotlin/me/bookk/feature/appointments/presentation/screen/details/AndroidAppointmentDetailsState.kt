package me.bookk.feature.appointments.presentation.screen.details

import me.bookk.designsystem.uistate.AndroidAppBarState
import me.bookk.designsystem.uistate.AndroidListState
import me.bookk.designsystem.uistate.AndroidNavigationState
import me.bookk.designsystem.uistate.AndroidNotificationState
import me.bookk.designsystem.uistate.AppBarState
import me.bookk.designsystem.uistate.ListState
import me.bookk.designsystem.uistate.NavigationState
import me.bookk.designsystem.uistate.PresentationNotificationState
import me.bookk.designsystem.uistate.simple.InfoLine

internal class AndroidAppointmentDetailsState : AppointmentDetailsState {
    override val appBar: AppBarState = AndroidAppBarState()

    override val infoSections: ListState<InfoLine> = AndroidListState()

    override val notifications: PresentationNotificationState = AndroidNotificationState()
    override val navigation: NavigationState<AppointmentDetailsDestination> = AndroidNavigationState()
}
