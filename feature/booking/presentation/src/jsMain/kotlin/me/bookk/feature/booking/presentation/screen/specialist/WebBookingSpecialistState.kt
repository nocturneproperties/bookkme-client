package me.bookk.feature.booking.presentation.screen.specialist

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import dev.icerock.moko.resources.desc.StringDesc
import me.bookk.site.designsystem.uistate.WebAppBarState
import me.bookk.site.designsystem.uistate.WebListState
import me.bookk.site.designsystem.uistate.WebNavigationState
import me.bookk.site.designsystem.uistate.WebNotificationState

internal class WebBookingSpecialistState : BookingSpecialistState {
    override val appBar = WebAppBarState()
    override var summary: StringDesc? by mutableStateOf(null)
    override val specialists = WebListState<SpecialistItem>()
    override val notifications = WebNotificationState()
    override val navigation = WebNavigationState<BookingSpecialistDestinations>()
}
