package me.bookk.feature.booking.presentation.screen.details

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import me.bookk.designsystem.uistate.WebAppBarState
import me.bookk.designsystem.uistate.WebButtonState
import me.bookk.designsystem.uistate.WebNavigationState
import me.bookk.designsystem.uistate.WebNotificationState

internal class WebBookingDetailsState : BookingDetailsState {
    override val appBar = WebAppBarState()
    override var booking: BookingDetailsItem? by mutableStateOf(null)
    override val actionButton = WebButtonState()
    override val notifications = WebNotificationState()
    override val navigation = WebNavigationState<BookingDetailsDestinations>()
}
