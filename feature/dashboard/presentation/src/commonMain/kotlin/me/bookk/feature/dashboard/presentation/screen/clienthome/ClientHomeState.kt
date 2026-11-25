package me.bookk.feature.dashboard.presentation.screen.clienthome

import dev.icerock.moko.resources.desc.StringDesc
import me.bookk.designsystem.resources.color.ColorToken
import me.bookk.designsystem.uistate.ButtonState
import me.bookk.designsystem.uistate.ListState
import me.bookk.designsystem.uistate.NavigationState
import me.bookk.designsystem.uistate.PresentationNotificationState

interface ClientHomeState {
    var greeting: StringDesc?
    val settingsButton: ButtonState
    val allBookingsButton: ButtonState
    val allBusinessesButton: ButtonState
    val upcoming: ListState<UpcomingBookingItem>
    val businesses: ListState<SavedBusinessItem>
    val notifications: PresentationNotificationState
    val navigation: NavigationState<ClientHomeDestinations>
}

class UpcomingBookingItem(
    val id: String,
    val weekday: String,
    val day: String,
    val businessName: String,
    val summary: StringDesc,
    val status: StringDesc,
    val statusColor: ColorToken,
    val onClick: () -> Unit,
)

class SavedBusinessItem(
    val id: String,
    val name: String,
    val subtitle: StringDesc,
    val onClick: () -> Unit,
)

val ClientHomeState.hasNoBusiness: Boolean
    get() = upcoming.isLoadedEmpty && businesses.isLoadedEmpty

val ClientHomeState.hasNoUpcomingBookings: Boolean
    get() = upcoming.isLoadedEmpty

val ClientHomeState.showsBookAgain: Boolean
    get() = !businesses.isLoadedEmpty

val ClientHomeState.isLoading: Boolean
    get() = greeting == null || upcoming.isLoading || businesses.isLoading

private val ListState<*>.isLoading: Boolean
    get() = isInitialLoading && errorState == null

private val ListState<*>.isLoadedEmpty: Boolean
    get() = !isInitialLoading && errorState == null && items.isEmpty()
