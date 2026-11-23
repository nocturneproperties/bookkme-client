package me.bookk.feature.booking.presentation.screen.specialist

import dev.icerock.moko.resources.desc.StringDesc
import me.bookk.designsystem.uistate.AppBarState
import me.bookk.designsystem.uistate.ButtonState
import me.bookk.designsystem.uistate.ListState
import me.bookk.designsystem.uistate.NavigationState
import me.bookk.designsystem.uistate.PresentationNotificationState
import me.bookk.feature.booking.presentation.screen.BookingTotal

interface BookingSpecialistState {
    val appBar: AppBarState
    var businessName: StringDesc?
    val specialists: ListState<SpecialistItem>
    var total: BookingTotal?
    val continueButton: ButtonState
    val notifications: PresentationNotificationState
    val navigation: NavigationState<BookingSpecialistDestinations>
}

class SpecialistItem(
    val id: String,
    val name: StringDesc,
    val initials: String,
    val isSelected: Boolean,
    val onClick: () -> Unit,
)
