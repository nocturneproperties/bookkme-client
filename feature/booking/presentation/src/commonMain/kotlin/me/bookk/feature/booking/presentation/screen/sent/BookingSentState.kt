package me.bookk.feature.booking.presentation.screen.sent

import dev.icerock.moko.resources.desc.StringDesc
import me.bookk.designsystem.resources.color.ColorToken
import me.bookk.designsystem.uistate.AppBarState
import me.bookk.designsystem.uistate.ButtonState
import me.bookk.designsystem.uistate.NavigationState
import me.bookk.designsystem.uistate.PresentationNotificationState

interface BookingSentState {
    val appBar: AppBarState
    var booking: SentBookingItem?
    val bookAgainButton: ButtonState
    val doneButton: ButtonState
    val notifications: PresentationNotificationState
    val navigation: NavigationState<BookingSentDestinations>
}

class SentBookingItem(
    val day: StringDesc,
    val details: StringDesc,
    val status: StringDesc,
    val statusColor: ColorToken,
    val onClick: () -> Unit,
)
