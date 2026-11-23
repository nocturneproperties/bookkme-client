package me.bookk.feature.booking.presentation.screen.specialist

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import dev.icerock.moko.resources.desc.StringDesc
import me.bookk.feature.booking.presentation.screen.BookingTotal
import me.bookk.site.designsystem.uistate.WebAppBarState
import me.bookk.site.designsystem.uistate.WebButtonState
import me.bookk.site.designsystem.uistate.WebListState
import me.bookk.site.designsystem.uistate.WebNavigationState
import me.bookk.site.designsystem.uistate.WebNotificationState

internal class WebBookingSpecialistState : BookingSpecialistState {
    override val appBar = WebAppBarState()
    override var businessName: StringDesc? by mutableStateOf(null)
    override val specialists = WebListState<SpecialistItem>()
    override var total: BookingTotal? by mutableStateOf(null)
    override val continueButton = WebButtonState()
    override val notifications = WebNotificationState()
    override val navigation = WebNavigationState<BookingSpecialistDestinations>()
}
