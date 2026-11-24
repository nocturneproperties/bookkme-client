package me.bookk.feature.booking.presentation.screen.review

import dev.icerock.moko.resources.desc.StringDesc
import me.bookk.designsystem.uistate.AppBarState
import me.bookk.designsystem.uistate.ButtonState
import me.bookk.designsystem.uistate.NavigationState
import me.bookk.designsystem.uistate.PresentationNotificationState
import me.bookk.designsystem.uistate.TextFieldState

interface BookingReviewState {
    val appBar: AppBarState
    var businessName: StringDesc?
    var summary: ReviewSummary?
    var businessMessage: BusinessMessage?
    val note: TextFieldState
    var confirmationNotice: StringDesc?
    val sendButton: ButtonState
    val notifications: PresentationNotificationState
    val navigation: NavigationState<BookingReviewDestinations>
}

data class ReviewSummary(
    val day: StringDesc,
    val details: StringDesc,
    val services: List<ReviewServiceItem>,
    val total: StringDesc,
)

data class ReviewServiceItem(
    val id: String,
    val name: StringDesc,
    val price: StringDesc,
    val previousPrice: StringDesc? = null,
)

data class BusinessMessage(
    val title: StringDesc,
    val text: StringDesc,
)
