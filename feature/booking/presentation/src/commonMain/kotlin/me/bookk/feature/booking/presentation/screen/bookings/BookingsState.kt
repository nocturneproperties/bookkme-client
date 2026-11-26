package me.bookk.feature.booking.presentation.screen.bookings

import dev.icerock.moko.resources.desc.StringDesc
import me.bookk.designsystem.resources.color.ColorToken
import me.bookk.designsystem.uistate.AppBarState
import me.bookk.designsystem.uistate.ListState
import me.bookk.designsystem.uistate.NavigationState
import me.bookk.designsystem.uistate.PresentationNotificationState
import me.bookk.designsystem.uistate.RadioButtonState

interface BookingsState {
    val appBar: AppBarState
    val upcomingTab: RadioButtonState
    val pastTab: RadioButtonState
    val upcoming: ListState<BookingsListItem>
    val past: ListState<BookingsListItem>
    val notifications: PresentationNotificationState
    val navigation: NavigationState<BookingsDestinations>
}

sealed interface BookingsListItem {
    val key: String

    data class Month(
        override val key: String,
        val title: String,
    ) : BookingsListItem

    class Booking(
        override val key: String,
        val day: String,
        val weekday: String,
        val dayColor: ColorToken,
        val businessName: String,
        val details: StringDesc,
        val status: StringDesc,
        val statusColor: ColorToken,
        val onClick: () -> Unit,
    ) : BookingsListItem
}
