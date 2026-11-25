package me.bookk.feature.booking.presentation.screen.bookings

import me.bookk.designsystem.uistate.WebAppBarState
import me.bookk.designsystem.uistate.WebListState
import me.bookk.designsystem.uistate.WebNavigationState
import me.bookk.designsystem.uistate.WebNotificationState
import me.bookk.designsystem.uistate.WebRadioButtonState

internal class WebBookingsState : BookingsState {
    override val appBar = WebAppBarState()
    override val upcomingTab = WebRadioButtonState()
    override val pastTab = WebRadioButtonState()
    override val upcoming = WebListState<BookingsListItem>()
    override val past = WebListState<BookingsListItem>()
    override val notifications = WebNotificationState()
    override val navigation = WebNavigationState<BookingsDestinations>()
}
