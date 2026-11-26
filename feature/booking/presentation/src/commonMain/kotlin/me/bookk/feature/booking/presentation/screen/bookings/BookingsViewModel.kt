package me.bookk.feature.booking.presentation.screen.bookings

import dev.icerock.moko.resources.desc.desc
import kotlinx.coroutines.flow.flowOn
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import me.bookk.android.designsystem.resources.DesignSystem
import me.bookk.android.feature.booking.resources.BookingRes
import me.bookk.core.coroutine.DispatcherProvider
import me.bookk.core.presentation.ViewModel
import me.bookk.core.presentation.VmArgs
import me.bookk.core.presentation.date.DateLocalizer
import me.bookk.core.presentation.memory.weakVMClosure
import me.bookk.designsystem.convenience.listPager
import me.bookk.designsystem.uistate.ListState
import me.bookk.designsystem.uistate.simple.EmptyState
import me.bookk.feature.booking.domain.api.GetBookings
import me.bookk.feature.booking.domain.api.entity.BookingOverview
import me.bookk.feature.booking.domain.api.entity.BookingPeriod
import me.bookk.feature.booking.presentation.BookingStateFactory
import kotlin.time.Clock

class BookingsViewModel(
    private val getBookings: GetBookings,
    private val clock: Clock,
    stateFactory: BookingStateFactory,
    dateLocalizer: DateLocalizer,
    vmArgs: VmArgs,
) : ViewModel(vmArgs) {

    private val rows = BookingsRows(dateLocalizer)

    val uiState: BookingsState = stateFactory.createBookingsState().setup()

    private val upcomingPager = listPager(uiState.upcoming, uiState.notifications, getBookings)
    private val pastPager = listPager(uiState.past, uiState.notifications, getBookings)

    init {
        observeBookings(BookingPeriod.UPCOMING)
        observeBookings(BookingPeriod.PAST)
        upcomingPager.load(BookingPeriod.UPCOMING)
        pastPager.load(BookingPeriod.PAST)
    }

    private fun BookingsState.setup() = apply {
        val strings = BookingRes.strings
        appBar.onBackClick = weakVMClosure { it.uiState.navigation.push(BookingsDestinations.Back) }
        upcomingTab.text = strings.booking_bookings_upcoming.desc()
        upcomingTab.onClick = weakVMClosure { it.selectTab(BookingPeriod.UPCOMING) }
        pastTab.text = strings.booking_bookings_past.desc()
        pastTab.onClick = weakVMClosure { it.selectTab(BookingPeriod.PAST) }
        upcoming.emptyState = EmptyState(
            image = DesignSystem.images.empty,
            label = strings.booking_bookings_upcoming_empty.desc(),
        )
        past.emptyState = EmptyState(
            image = DesignSystem.images.empty,
            label = strings.booking_bookings_past_empty.desc(),
        )
        showTab(BookingPeriod.UPCOMING)
    }

    private fun selectTab(period: BookingPeriod) {
        uiState.showTab(period)
    }

    private fun BookingsState.showTab(period: BookingPeriod) {
        upcomingTab.isSelected = period == BookingPeriod.UPCOMING
        upcoming.isVisible = period == BookingPeriod.UPCOMING
        pastTab.isSelected = period == BookingPeriod.PAST
        past.isVisible = period == BookingPeriod.PAST
    }

    private fun observeBookings(period: BookingPeriod) {
        getBookings.flow(period)
            .flowOn(DispatcherProvider.io)
            .safeOnEach { renderBookings(period, it) }
            .onError { uiState.notifications.add(it.notification()) }
            .observe()
    }

    private fun renderBookings(period: BookingPeriod, bookings: List<BookingOverview>) {
        val list = period.list()
        if (bookings.isEmpty() && list.isInitialLoading) return
        val today = clock.now().toLocalDateTime(TimeZone.currentSystemDefault()).date
        list.replace(
            rows.items(
                period = period,
                bookings = bookings,
                today = today,
                onClick = weakVMClosure { vm, booking -> vm.open(booking) },
            ),
        )
    }

    private fun BookingPeriod.list(): ListState<BookingsListItem> {
        return when (this) {
            BookingPeriod.UPCOMING -> uiState.upcoming
            BookingPeriod.PAST -> uiState.past
        }
    }

    private fun open(booking: BookingOverview) {
        val destination = when (booking.status) {
            is BookingOverview.Status.Request -> {
                BookingsDestinations.RequestDetails(booking.id)
            }

            is BookingOverview.Status.Appointment -> {
                BookingsDestinations.AppointmentDetails(booking.id)
            }
        }
        uiState.navigation.push(destination)
    }
}
