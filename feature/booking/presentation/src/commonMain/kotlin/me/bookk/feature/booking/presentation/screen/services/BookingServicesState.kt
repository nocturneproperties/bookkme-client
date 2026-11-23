package me.bookk.feature.booking.presentation.screen.services

import dev.icerock.moko.resources.desc.StringDesc
import me.bookk.designsystem.uistate.AppBarState
import me.bookk.designsystem.uistate.ButtonState
import me.bookk.designsystem.uistate.ListState
import me.bookk.designsystem.uistate.NavigationState
import me.bookk.designsystem.uistate.OptionsMultiPickerState
import me.bookk.designsystem.uistate.PickerPresentation
import me.bookk.designsystem.uistate.PresentationNotificationState
import me.bookk.feature.booking.domain.api.entity.BookingService
import me.bookk.feature.booking.presentation.screen.BookingTotal

interface BookingServicesState {
    val appBar: AppBarState
    var businessName: StringDesc?
    val groups: ListState<ServiceGroupItem>
    val services: OptionsMultiPickerState<ServiceOption>
    var total: BookingTotal?
    val continueButton: ButtonState
    val notifications: PresentationNotificationState
    val navigation: NavigationState<BookingServicesDestinations>
}

class ServiceGroupItem(
    val id: String,
    val name: StringDesc,
    val services: List<ServiceOption>,
)

data class ServiceOption(
    override val pickerItemId: String,
    override val displayName: StringDesc,
    val details: StringDesc,
    val service: BookingService,
) : PickerPresentation()
