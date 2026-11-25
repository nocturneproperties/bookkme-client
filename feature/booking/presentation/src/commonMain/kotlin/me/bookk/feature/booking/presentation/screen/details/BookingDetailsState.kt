package me.bookk.feature.booking.presentation.screen.details

import dev.icerock.moko.resources.desc.StringDesc
import me.bookk.designsystem.resources.color.ColorToken
import me.bookk.designsystem.uistate.AppBarState
import me.bookk.designsystem.uistate.ButtonState
import me.bookk.designsystem.uistate.NavigationState
import me.bookk.designsystem.uistate.PresentationNotificationState

interface BookingDetailsState {
    val appBar: AppBarState
    var booking: BookingDetailsItem?
    val actionButton: ButtonState
    val notifications: PresentationNotificationState
    val navigation: NavigationState<BookingDetailsDestinations>
}

data class BookingDetailsItem(
    val businessName: StringDesc,
    val status: StringDesc,
    val statusColor: ColorToken,
    val rows: List<BookingDetailsRow>,
    val services: List<BookingDetailsServiceItem>,
    val total: StringDesc,
    val statusExplanation: StringDesc,
)

data class BookingDetailsRow(
    val title: StringDesc,
    val value: StringDesc,
)

data class BookingDetailsServiceItem(
    val name: StringDesc,
    val price: StringDesc,
)
