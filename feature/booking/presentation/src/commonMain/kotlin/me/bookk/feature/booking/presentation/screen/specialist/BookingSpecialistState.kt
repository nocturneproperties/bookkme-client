package me.bookk.feature.booking.presentation.screen.specialist

import dev.icerock.moko.resources.desc.StringDesc
import me.bookk.designsystem.uistate.AppBarState
import me.bookk.designsystem.uistate.ListState
import me.bookk.designsystem.uistate.NavigationState
import me.bookk.designsystem.uistate.PresentationNotificationState

interface BookingSpecialistState {
    val appBar: AppBarState
    var summary: StringDesc?
    val specialists: ListState<SpecialistItem>
    val notifications: PresentationNotificationState
    val navigation: NavigationState<BookingSpecialistDestinations>
}

class SpecialistItem(
    val id: String,
    val name: StringDesc,
    val initials: String,
    val onClick: () -> Unit,
)
