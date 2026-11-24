package me.bookk.feature.booking.presentation.screen.sent

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import me.bookk.designsystem.uistate.WebAppBarState
import me.bookk.designsystem.uistate.WebButtonState
import me.bookk.designsystem.uistate.WebNavigationState
import me.bookk.designsystem.uistate.WebNotificationState

internal class WebBookingSentState : BookingSentState {
    override val appBar = WebAppBarState()
    override var booking: SentBookingItem? by mutableStateOf(null)
    override val bookAgainButton = WebButtonState()
    override val doneButton = WebButtonState()
    override val notifications = WebNotificationState()
    override val navigation = WebNavigationState<BookingSentDestinations>()
}
