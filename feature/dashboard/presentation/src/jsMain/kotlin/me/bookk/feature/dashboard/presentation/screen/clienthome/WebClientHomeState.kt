package me.bookk.feature.dashboard.presentation.screen.clienthome

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import dev.icerock.moko.resources.desc.StringDesc
import me.bookk.designsystem.uistate.WebButtonState
import me.bookk.designsystem.uistate.WebListState
import me.bookk.designsystem.uistate.WebNavigationState
import me.bookk.designsystem.uistate.WebNotificationState

internal class WebClientHomeState : ClientHomeState {
    override var greeting: StringDesc? by mutableStateOf(null)
    override val settingsButton = WebButtonState()
    override val allBookingsButton = WebButtonState()
    override val upcoming = WebListState<UpcomingBookingItem>()
    override val businesses = WebListState<SavedBusinessItem>()
    override val notifications = WebNotificationState()
    override val navigation = WebNavigationState<ClientHomeDestinations>()
}
