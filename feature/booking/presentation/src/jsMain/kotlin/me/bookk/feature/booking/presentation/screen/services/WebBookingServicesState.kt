package me.bookk.feature.booking.presentation.screen.services

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
import me.bookk.site.designsystem.uistate.WebOptionsMultiPickerState

internal class WebBookingServicesState : BookingServicesState {
    override val appBar = WebAppBarState()
    override var businessName: StringDesc? by mutableStateOf(null)
    override val groups = WebListState<ServiceGroupItem>()
    override val services = WebOptionsMultiPickerState<ServiceOption>()
    override var total: BookingTotal? by mutableStateOf(null)
    override val continueButton = WebButtonState()
    override val notifications = WebNotificationState()
    override val navigation = WebNavigationState<BookingServicesDestinations>()
}
