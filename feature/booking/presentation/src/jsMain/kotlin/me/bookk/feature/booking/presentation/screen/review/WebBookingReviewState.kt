package me.bookk.feature.booking.presentation.screen.review

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import dev.icerock.moko.resources.desc.StringDesc
import me.bookk.designsystem.uistate.WebAppBarState
import me.bookk.designsystem.uistate.WebButtonState
import me.bookk.designsystem.uistate.WebNavigationState
import me.bookk.designsystem.uistate.WebNotificationState
import me.bookk.designsystem.uistate.WebTextFieldState

internal class WebBookingReviewState : BookingReviewState {
    override val appBar = WebAppBarState()
    override var businessName: StringDesc? by mutableStateOf(null)
    override var summary: ReviewSummary? by mutableStateOf(null)
    override var businessMessage: BusinessMessage? by mutableStateOf(null)
    override val note = WebTextFieldState()
    override var confirmationNotice: StringDesc? by mutableStateOf(null)
    override val sendButton = WebButtonState()
    override val notifications = WebNotificationState()
    override val navigation = WebNavigationState<BookingReviewDestinations>()
}
