package me.bookk.feature.dashboard.presentation

import dev.icerock.moko.resources.desc.StringDesc
import dev.icerock.moko.resources.desc.desc
import me.bookk.designsystem.test.FakeButtonState
import me.bookk.designsystem.test.FakeListState
import me.bookk.designsystem.test.FakeNavigationState
import me.bookk.designsystem.test.FakeNotificationState
import me.bookk.feature.dashboard.presentation.screen.clienthome.ClientHomeDestinations
import me.bookk.feature.dashboard.presentation.screen.clienthome.ClientHomeState
import me.bookk.feature.dashboard.presentation.screen.clienthome.SavedBusinessItem
import me.bookk.feature.dashboard.presentation.screen.clienthome.UpcomingBookingItem

class FakeClientDashboardStateFactory : ClientDashboardStateFactory {
    override fun createClientHomeState(): ClientHomeState {
        return FakeClientHomeState()
    }
}

class FakeClientHomeState : ClientHomeState {
    override var greeting: StringDesc? = null
    override val settingsButton = FakeButtonState()
    override val allBookingsButton = FakeButtonState()
    override val allBusinessesButton = FakeButtonState()
    override val upcoming = FakeListState<UpcomingBookingItem>()
    override val businesses = FakeListState<SavedBusinessItem>()
    override val notifications = FakeNotificationState()
    override val navigation = FakeNavigationState<ClientHomeDestinations>()
}
