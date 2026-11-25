package me.bookk.feature.booking.presentation.screen.bookings

import dev.icerock.moko.resources.desc.desc
import dev.icerock.moko.resources.desc.joinToStringDesc
import dev.icerock.moko.resources.format
import dev.mokkery.answering.returns
import dev.mokkery.every
import dev.mokkery.everySuspend
import dev.mokkery.matcher.any
import dev.mokkery.mock
import dev.mokkery.verifySuspend
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant
import library.money.api.Money
import me.bookk.android.feature.booking.resources.BookingRes
import me.bookk.core.domain.pagination.Page
import me.bookk.core.presentation.VmArgs
import me.bookk.core.presentation.date.DateLocalizer
import me.bookk.core.presentation.date.DateStyle
import me.bookk.core.test.given
import me.bookk.core.test.runUnitTest
import me.bookk.core.test.then
import me.bookk.core.test.whenn
import me.bookk.designsystem.resources.color.ColorToken
import me.bookk.designsystem.test.FakeDateLocalizer
import me.bookk.designsystem.test.FakeErrorMapper
import me.bookk.designsystem.test.ViewModelTestDispatchers
import me.bookk.feature.booking.domain.api.GetBookings
import me.bookk.feature.booking.domain.api.entity.BookingAppointmentDetails
import me.bookk.feature.booking.domain.api.entity.BookingOverview
import me.bookk.feature.booking.domain.api.entity.BookingPeriod
import me.bookk.feature.booking.domain.api.entity.BookingRequestDetails
import me.bookk.feature.booking.domain.api.entity.BookingSummary
import me.bookk.feature.booking.domain.api.entity.BookingSummary.BookedService
import me.bookk.feature.booking.presentation.FakeBookingStateFactory
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue
import kotlin.time.Clock
import kotlin.time.Instant
import kotlin.uuid.Uuid

class BookingsViewModelTest {

    private val dispatchers = ViewModelTestDispatchers()

    @BeforeTest
    fun setUp() {
        dispatchers.install()
    }

    @AfterTest
    fun tearDown() {
        dispatchers.uninstall()
    }

    private val price = Money(60_000, Money.SupportedCurrency.UAH)
    private val now = LocalDateTime(2026, 11, 25, 9, 0).toInstant(TimeZone.currentSystemDefault())

    private inner class Fixture {
        val upcoming = MutableStateFlow<List<BookingOverview>>(emptyList())
        val past = MutableStateFlow<List<BookingOverview>>(emptyList())
        val getBookings = mock<GetBookings> {
            every { flow(BookingPeriod.UPCOMING) } returns upcoming
            every { flow(BookingPeriod.PAST) } returns past
            everySuspend { refresh(any()) } returns Page(emptyList(), null)
        }
        var dateLocalizer: DateLocalizer = FakeDateLocalizer()

        fun sut(): BookingsViewModel {
            return BookingsViewModel(
                getBookings = getBookings,
                stateFactory = FakeBookingStateFactory(),
                dateLocalizer = dateLocalizer,
                clock = object : Clock {
                    override fun now(): Instant = this@BookingsViewModelTest.now
                },
                vmArgs = VmArgs(FakeErrorMapper()),
            )
        }
    }

    private fun booking(
        startsAt: LocalDateTime,
        status: BookingOverview.Status = waiting,
        services: List<BookedService> = listOf(BookedService("Haircut", 1, price)),
    ): BookingOverview {
        return BookingOverview(
            id = Uuid.random(),
            summary = BookingSummary(
                businessName = "Studio Lumen",
                specialistName = "Anna Kovalenko",
                startsAt = startsAt,
                endsAt = startsAt,
                note = "",
                services = services,
                total = price,
            ),
            status = status,
        )
    }

    private val waiting = BookingOverview.Status.Request(
        BookingRequestDetails.Status.WAITING_FOR_CONFIRMATION,
    )
    private val confirmed = BookingOverview.Status.Appointment(
        BookingAppointmentDetails.Status.CONFIRMED,
    )

    private val BookingsState.upcomingBookings: List<BookingsListItem.Booking>
        get() = upcoming.items.filterIsInstance<BookingsListItem.Booking>()

    private val BookingsState.pastBookings: List<BookingsListItem.Booking>
        get() = past.items.filterIsInstance<BookingsListItem.Booking>()

    @Test
    fun `opens on the upcoming tab`() = runUnitTest {
        given()
        val fixture = Fixture()

        whenn()
        val state = fixture.sut().uiState

        then()
        assertTrue(state.upcomingTab.isSelected)
        assertFalse(state.pastTab.isSelected)
        assertTrue(state.upcoming.isVisible)
        assertFalse(state.past.isVisible)
    }

    @Test
    fun `picking the past tab shows the past bookings instead`() = runUnitTest {
        given()
        val fixture = Fixture()
        val state = fixture.sut().uiState

        whenn()
        state.pastTab.onClick?.invoke()

        then()
        assertTrue(state.pastTab.isSelected)
        assertFalse(state.upcomingTab.isSelected)
        assertTrue(state.past.isVisible)
        assertFalse(state.upcoming.isVisible)
    }

    @Test
    fun `names the tabs`() = runUnitTest {
        given()
        val fixture = Fixture()

        whenn()
        val state = fixture.sut().uiState

        then()
        assertEquals(BookingRes.strings.booking_bookings_upcoming.desc(), state.upcomingTab.text)
        assertEquals(BookingRes.strings.booking_bookings_past.desc(), state.pastTab.text)
    }

    @Test
    fun `loads the first page of both periods`() = runUnitTest {
        given()
        val fixture = Fixture()

        whenn()
        fixture.sut()

        then()
        verifySuspend {
            fixture.getBookings.refresh(BookingPeriod.UPCOMING)
            fixture.getBookings.refresh(BookingPeriod.PAST)
        }
    }

    @Test
    fun `groups the bookings under their month`() = runUnitTest {
        given()
        val fixture = Fixture()
        val first = booking(LocalDateTime(2026, 11, 26, 14, 30))
        val second = booking(LocalDateTime(2026, 11, 27, 11, 0))
        val third = booking(LocalDateTime(2026, 12, 3, 18, 15))
        fixture.upcoming.value = listOf(first, second, third)

        whenn()
        val state = fixture.sut().uiState

        then()
        assertEquals(
            listOf("2026-11-01", first.id, second.id, "2026-12-01", third.id),
            state.upcoming.items.map {
                when (it) {
                    is BookingsListItem.Month -> it.title
                    is BookingsListItem.Booking -> Uuid.parse(it.key)
                }
            },
        )
    }

    @Test
    fun `adds the year to months of another year`() = runUnitTest {
        given()
        val fixture = Fixture()
        fixture.dateLocalizer = PatternDateLocalizer()
        fixture.upcoming.value = listOf(
            booking(LocalDateTime(2026, 12, 3, 18, 15)),
            booking(LocalDateTime(2027, 1, 5, 10, 0)),
        )

        whenn()
        val state = fixture.sut().uiState

        then()
        assertEquals(
            listOf("LLLL:2026-12-01", "LLLL yyyy:2027-01-01"),
            state.upcoming.items.filterIsInstance<BookingsListItem.Month>().map { it.title },
        )
    }

    @Test
    fun `shows the day, business, start time and services of a booking`() = runUnitTest {
        given()
        val fixture = Fixture()
        fixture.upcoming.value = listOf(
            booking(
                LocalDateTime(2026, 11, 26, 14, 30),
                services = listOf(
                    BookedService("Haircut", 1, price),
                    BookedService("Beard trim", 2, price),
                ),
            ),
        )

        whenn()
        val item = fixture.sut().uiState.upcomingBookings.single()

        then()
        val strings = BookingRes.strings
        assertEquals("26", item.day)
        assertEquals("2026-11-26", item.weekday)
        assertEquals("Studio Lumen", item.businessName)
        assertEquals(
            strings.booking_review_details.format(
                "14:30",
                listOf(
                    "Haircut".desc(),
                    strings.booking_review_service_count.format("Beard trim", 2),
                ).joinToStringDesc(", "),
            ),
            item.details,
        )
    }

    @Test
    fun `shows each booking's status with its colour`() = runUnitTest {
        given()
        val fixture = Fixture()
        fixture.past.value = listOf(
            booking(
                LocalDateTime(2026, 11, 20, 10, 0),
                BookingOverview.Status.Request(BookingRequestDetails.Status.DECLINED),
            ),
            booking(
                LocalDateTime(2026, 11, 12, 10, 0),
                BookingOverview.Status.Appointment(BookingAppointmentDetails.Status.COMPLETED),
            ),
        )

        whenn()
        val items = fixture.sut().uiState.pastBookings

        then()
        val strings = BookingRes.strings
        assertEquals(
            listOf(
                strings.booking_details_status_declined.desc() to ColorToken.Error,
                strings.booking_details_status_done.desc() to ColorToken.SecondaryText,
            ),
            items.map { it.status to it.statusColor },
        )
    }

    @Test
    fun `greys out the day of past bookings only`() = runUnitTest {
        given()
        val fixture = Fixture()
        fixture.upcoming.value = listOf(booking(LocalDateTime(2026, 11, 26, 14, 30), confirmed))
        fixture.past.value = listOf(booking(LocalDateTime(2026, 11, 12, 10, 0), confirmed))

        whenn()
        val state = fixture.sut().uiState

        then()
        assertEquals(ColorToken.PrimaryText, state.upcomingBookings.single().dayColor)
        assertEquals(ColorToken.SecondaryText, state.pastBookings.single().dayColor)
    }

    @Test
    fun `tapping a request opens the request's details`() = runUnitTest {
        given()
        val fixture = Fixture()
        val request = booking(LocalDateTime(2026, 11, 26, 14, 30), waiting)
        fixture.upcoming.value = listOf(request)
        val state = fixture.sut().uiState

        whenn()
        state.upcomingBookings.single().onClick()

        then()
        assertEquals(
            listOf(BookingsDestinations.RequestDetails(request.id)),
            state.navigation.navigationDestination,
        )
    }

    @Test
    fun `tapping an appointment opens the appointment's details`() = runUnitTest {
        given()
        val fixture = Fixture()
        val appointment = booking(LocalDateTime(2026, 11, 26, 14, 30), confirmed)
        fixture.upcoming.value = listOf(appointment)
        val state = fixture.sut().uiState

        whenn()
        state.upcomingBookings.single().onClick()

        then()
        assertEquals(
            listOf(BookingsDestinations.AppointmentDetails(appointment.id)),
            state.navigation.navigationDestination,
        )
    }

    @Test
    fun `loads the next page of a period at the end of its list`() = runUnitTest {
        given()
        val fixture = Fixture()
        everySuspend { fixture.getBookings.refresh(BookingPeriod.PAST) } returns
            Page(listOf(booking(LocalDateTime(2026, 11, 12, 10, 0))), "next")
        everySuspend { fixture.getBookings.loadMore(any(), any()) } returns Page(emptyList(), null)
        val state = fixture.sut().uiState
        advanceUntilIdle()

        whenn()
        state.past.loadMore?.invoke()

        then()
        verifySuspend { fixture.getBookings.loadMore(BookingPeriod.PAST, "next") }
    }

    @Test
    fun `offers no next page once a period has ended`() = runUnitTest {
        given()
        val fixture = Fixture()

        whenn()
        val state = fixture.sut().uiState

        then()
        assertEquals(null, state.upcoming.loadMore)
    }

    @Test
    fun `tells each tab apart when it has no bookings`() = runUnitTest {
        given()
        val fixture = Fixture()

        whenn()
        val state = fixture.sut().uiState

        then()
        val upcomingEmpty = assertNotNull(state.upcoming.emptyState)
        val pastEmpty = assertNotNull(state.past.emptyState)
        assertNotEquals(upcomingEmpty.label, pastEmpty.label)
    }

    @Test
    fun `back leaves the screen`() = runUnitTest {
        given()
        val fixture = Fixture()
        val state = fixture.sut().uiState

        whenn()
        state.appBar.onBackClick?.invoke()

        then()
        assertEquals(listOf(BookingsDestinations.Back), state.navigation.navigationDestination)
    }

    private class PatternDateLocalizer : DateLocalizer {
        override fun forStyle(dateStyle: DateStyle): DateLocalizer.Formatter {
            return Formatter(dateStyle.name)
        }

        override fun strict(
            pattern: String,
            respectUserSettings: Boolean,
        ): DateLocalizer.Formatter {
            return Formatter(pattern)
        }

        private class Formatter(
            private val pattern: String,
        ) : DateLocalizer.Formatter {
            override fun format(date: LocalDateTime, relative: Boolean): String {
                return "$pattern:$date"
            }

            override fun format(date: LocalDate, relative: Boolean): String {
                return "$pattern:$date"
            }

            override fun format(time: LocalTime): String {
                return "$pattern:$time"
            }
        }
    }
}
