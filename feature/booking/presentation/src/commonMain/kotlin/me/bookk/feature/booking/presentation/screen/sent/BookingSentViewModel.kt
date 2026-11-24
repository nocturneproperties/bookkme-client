package me.bookk.feature.booking.presentation.screen.sent

import dev.icerock.moko.resources.desc.StringDesc
import dev.icerock.moko.resources.desc.desc
import dev.icerock.moko.resources.desc.joinToStringDesc
import dev.icerock.moko.resources.format
import kotlinx.coroutines.flow.flowOn
import me.bookk.android.feature.booking.resources.BookingRes
import me.bookk.core.coroutine.DispatcherProvider
import me.bookk.core.presentation.ViewModel
import me.bookk.core.presentation.VmArgs
import me.bookk.core.presentation.date.DateLocalizer
import me.bookk.core.presentation.memory.weakVMClosure
import me.bookk.designsystem.resources.color.ColorToken
import me.bookk.feature.booking.domain.api.GetSentBooking
import me.bookk.feature.booking.domain.api.entity.SentBooking
import me.bookk.feature.booking.domain.api.entity.SentBooking.BookedService
import me.bookk.feature.booking.domain.api.entity.SentBookingRequest
import me.bookk.feature.booking.presentation.BookingStateFactory
import me.bookk.feature.booking.presentation.screen.bookingDayFormat
import me.bookk.feature.booking.presentation.screen.slotTimeFormat
import org.koin.core.annotation.InjectedParam

class BookingSentViewModel(
    @InjectedParam private val request: SentBookingRequest,
    private val getSentBooking: GetSentBooking,
    dateLocalizer: DateLocalizer,
    stateFactory: BookingStateFactory,
    vmArgs: VmArgs,
) : ViewModel(vmArgs) {

    private val dayFormat = dateLocalizer.bookingDayFormat()
    private val timeFormat = dateLocalizer.slotTimeFormat()

    val uiState: BookingSentState = stateFactory.createBookingSentState().setup()

    init {
        observeSentBooking()
    }

    private fun BookingSentState.setup() = apply {
        appBar.onBackClick = weakVMClosure { it.returnHome() }
        doneButton.text = BookingRes.strings.booking_sent_done.desc()
        doneButton.onClick = weakVMClosure { it.returnHome() }
        bookAgainButton.text = BookingRes.strings.booking_sent_book_again.desc()
        bookAgainButton.onClick = weakVMClosure {
            it.uiState.navigation.push(BookingSentDestinations.Services(it.request.businessId))
        }
    }

    private fun returnHome() {
        uiState.navigation.push(BookingSentDestinations.Home)
    }

    private fun observeSentBooking() {
        getSentBooking.flow(request)
            .flowOn(DispatcherProvider.io)
            .safeOnEach { booking -> uiState.booking = booking?.toItem() }
            .onError { uiState.notifications.add(it.notification()) }
            .observe()
    }

    private fun SentBooking.toItem(): SentBookingItem {
        return SentBookingItem(
            day = dayFormat.format(startsAt.date).desc(),
            details = details(),
            status = status.label(),
            statusColor = status.color(),
            onClick = weakVMClosure {
                it.uiState.navigation.push(BookingSentDestinations.BookingDetails(id))
            },
        )
    }

    private fun SentBooking.details(): StringDesc {
        val strings = BookingRes.strings
        val timeRange = strings.booking_review_time_range.format(
            timeFormat.format(startsAt.time),
            timeFormat.format(endsAt.time),
        )
        val serviceNames = services.map { it.name() }.joinToStringDesc(SERVICE_SEPARATOR)
        return strings.booking_review_details.format(timeRange, serviceNames)
    }

    private fun BookedService.name(): StringDesc {
        return when (quantity) {
            1 -> name.desc()
            else -> BookingRes.strings.booking_review_service_count.format(name, quantity)
        }
    }

    private fun SentBooking.Status.label(): StringDesc {
        return when (this) {
            SentBooking.Status.WAITING_FOR_CONFIRMATION -> {
                BookingRes.strings.booking_sent_status_waiting.desc()
            }

            SentBooking.Status.CONFIRMED -> {
                BookingRes.strings.booking_sent_status_confirmed.desc()
            }
        }
    }

    private fun SentBooking.Status.color(): ColorToken {
        return when (this) {
            SentBooking.Status.WAITING_FOR_CONFIRMATION -> ColorToken.SecondaryText
            SentBooking.Status.CONFIRMED -> ColorToken.Success
        }
    }

    private companion object {
        const val SERVICE_SEPARATOR = ", "
    }
}
