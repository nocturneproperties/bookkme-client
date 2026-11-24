package me.bookk.feature.booking.presentation.screen.specialist

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import dev.icerock.moko.resources.desc.StringDesc
import me.bookk.designsystem.uistate.WebAppBarState
import me.bookk.designsystem.uistate.WebButtonState
import me.bookk.designsystem.uistate.WebListState
import me.bookk.designsystem.uistate.WebNavigationState
import me.bookk.designsystem.uistate.WebNotificationState
import me.bookk.feature.booking.presentation.screen.BookingTotal

internal class WebBookingSpecialistState : BookingSpecialistState {
    override val appBar = WebAppBarState()
    override var businessName: StringDesc? by mutableStateOf(null)
    override val specialists = WebListState<SpecialistItem>()
    override var total: BookingTotal? by mutableStateOf(null)
    override val continueButton = WebButtonState()
    override val notifications = WebNotificationState()
    override val navigation = WebNavigationState<BookingSpecialistDestinations>()
}
