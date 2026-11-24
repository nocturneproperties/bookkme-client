package me.bookk.feature.booking.presentation.screen.datetime

import dev.icerock.moko.resources.desc.StringDesc
import me.bookk.designsystem.uistate.AppBarState
import me.bookk.designsystem.uistate.ButtonState
import me.bookk.designsystem.uistate.DatePickerState
import me.bookk.designsystem.uistate.ListState
import me.bookk.designsystem.uistate.NavigationState
import me.bookk.designsystem.uistate.PresentationNotificationState
import me.bookk.feature.booking.presentation.screen.BookingTotal

interface BookingDateTimeState {
    val appBar: AppBarState
    var businessName: StringDesc?
    var specialistName: StringDesc?
    var monthTitle: StringDesc?
    val pickDateButton: ButtonState
    val datePicker: DatePickerState
    var days: List<BookingDayItem>
    val slots: ListState<SlotGroupItem>
    var total: BookingTotal?
    val continueButton: ButtonState
    val notifications: PresentationNotificationState
    val navigation: NavigationState<BookingDateTimeDestinations>
}

class BookingDayItem(
    val id: String,
    val weekday: StringDesc,
    val dayOfMonth: StringDesc,
    val isToday: Boolean,
    val isSelected: Boolean,
    val isEnabled: Boolean,
    val isClosed: Boolean,
    val onClick: () -> Unit,
)

class SlotGroupItem(
    val id: String,
    val title: StringDesc,
    val slots: List<SlotItem>,
)

class SlotItem(
    val id: String,
    val time: StringDesc,
    val isSelected: Boolean,
    val onClick: () -> Unit,
)
