package me.bookk.feature.appointments.presentation.screen.requestlist

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import kotlinx.datetime.LocalDate
import me.bookk.core.presentation.date.today
import me.bookk.designsystem.uistate.AndroidAppBarState
import me.bookk.designsystem.uistate.AndroidListState
import me.bookk.designsystem.uistate.AndroidNavigationState
import me.bookk.designsystem.uistate.AndroidNotificationState
import me.bookk.designsystem.uistate.AndroidRefreshState
import me.bookk.designsystem.uistate.AppBarState
import me.bookk.designsystem.uistate.ListState
import me.bookk.designsystem.uistate.NavigationState
import me.bookk.designsystem.uistate.PresentationNotificationState
import me.bookk.designsystem.uistate.RefreshState

internal class AndroidAppointmentListState : AppointmentListState {
    override val appBar: AppBarState = AndroidAppBarState()
    override var selectedDate: LocalDate by mutableStateOf(LocalDate.today())
    override val appointments: ListState<AppointmentItemState> = AndroidListState()
    override val refresh: RefreshState = AndroidRefreshState()
    override val notifications: PresentationNotificationState = AndroidNotificationState()
    override val navigation: NavigationState<AppointmentListDestinations> = AndroidNavigationState()
}
