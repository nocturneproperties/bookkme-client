package me.bookk.feature.booking.presentation.screen.services

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import dev.icerock.moko.resources.desc.StringDesc
import me.bookk.designsystem.uistate.WebAppBarState
import me.bookk.designsystem.uistate.WebButtonState
import me.bookk.designsystem.uistate.WebListState
import me.bookk.designsystem.uistate.WebNavigationState
import me.bookk.designsystem.uistate.WebNotificationState
import me.bookk.designsystem.uistate.WebOptionsMultiPickerState
import me.bookk.feature.booking.presentation.screen.BookingTotal

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
