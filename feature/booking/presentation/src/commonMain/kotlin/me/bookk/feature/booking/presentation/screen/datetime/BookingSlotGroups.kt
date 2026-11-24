package me.bookk.feature.booking.presentation.screen.datetime

import dev.icerock.moko.resources.StringResource
import dev.icerock.moko.resources.desc.StringDesc
import dev.icerock.moko.resources.desc.desc
import dev.icerock.moko.resources.format
import kotlinx.datetime.LocalTime
import me.bookk.android.designsystem.resources.DesignSystem
import me.bookk.android.feature.booking.resources.BookingRes
import me.bookk.core.presentation.date.DateLocalizer
import me.bookk.designsystem.uistate.ListState
import me.bookk.designsystem.uistate.simple.EmptyState
import me.bookk.feature.booking.domain.api.entity.BookingDay
import me.bookk.feature.booking.domain.api.entity.BookingSlot
import me.bookk.feature.booking.presentation.screen.slotTimeFormat

internal class BookingSlotGroups(
    dateLocalizer: DateLocalizer,
) {

    private val timeFormat = dateLocalizer.slotTimeFormat()

    fun groups(
        slots: List<BookingSlot>,
        selected: BookingSlot?,
        onSlotClick: (BookingSlot) -> Unit,
    ): List<SlotGroupItem> {
        val byPeriod = slots.groupBy { DayPeriod.of(it.time) }
        return DayPeriod.entries.mapNotNull { period ->
            byPeriod[period]?.let { periodSlots ->
                SlotGroupItem(
                    id = period.name,
                    title = period.title.desc(),
                    slots = periodSlots.map { slotItem(it, it == selected, onSlotClick) },
                )
            }
        }
    }

    private fun slotItem(
        slot: BookingSlot,
        isSelected: Boolean,
        onSlotClick: (BookingSlot) -> Unit,
    ): SlotItem {
        return SlotItem(
            id = slot.itemId(),
            time = timeFormat.format(slot.time).desc(),
            isSelected = isSelected,
            onClick = { onSlotClick(slot) },
        )
    }

    private enum class DayPeriod(
        val title: StringResource,
    ) {
        MORNING(BookingRes.strings.booking_date_time_morning),
        AFTERNOON(BookingRes.strings.booking_date_time_afternoon),
        EVENING(BookingRes.strings.booking_date_time_evening),
        ;

        companion object {
            private val AFTERNOON_START = LocalTime(12, 0)
            private val EVENING_START = LocalTime(17, 0)

            fun of(time: LocalTime): DayPeriod {
                return when {
                    time < AFTERNOON_START -> MORNING
                    time < EVENING_START -> AFTERNOON
                    else -> EVENING
                }
            }
        }
    }
}

internal fun List<BookingSlot>.shownAsSelectedIn(slots: ListState<SlotGroupItem>): BookingSlot? {
    val selectedId = slots.items
        .flatMap { it.slots }
        .firstOrNull { it.isSelected }
        ?.id
    return firstOrNull { it.itemId() == selectedId }
}

internal fun BookingDay.noSlotsState(businessName: String): EmptyState {
    return slotsEmptyState(
        when (isBusinessClosed) {
            true -> BookingRes.strings.booking_date_time_closed.format(businessName)
            false -> BookingRes.strings.booking_date_time_no_slots.desc()
        },
    )
}

internal fun bookingUnavailableState(businessName: String): EmptyState {
    return slotsEmptyState(BookingRes.strings.booking_date_time_unavailable.format(businessName))
}

private fun slotsEmptyState(label: StringDesc): EmptyState {
    return EmptyState(image = DesignSystem.images.empty, label = label)
}

private fun BookingSlot.itemId(): String {
    return start.toString()
}
