package me.bookk.feature.booking.presentation.screen.sent

import dev.icerock.moko.resources.desc.desc
import dev.icerock.moko.resources.desc.joinToStringDesc
import dev.icerock.moko.resources.format
import dev.mokkery.answering.returns
import dev.mokkery.every
import dev.mokkery.mock
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.datetime.LocalDateTime
import me.bookk.android.feature.booking.resources.BookingRes
import me.bookk.core.presentation.VmArgs
import me.bookk.core.test.given
import me.bookk.core.test.runUnitTest
import me.bookk.core.test.then
import me.bookk.core.test.whenn
import me.bookk.designsystem.resources.color.ColorToken
import me.bookk.designsystem.test.FakeDateLocalizer
import me.bookk.designsystem.test.FakeErrorMapper
import me.bookk.designsystem.test.TestException
import me.bookk.designsystem.test.ViewModelTestDispatchers
import me.bookk.designsystem.test.assertMappedSingle
import me.bookk.designsystem.test.failOnceThenSuspend
import me.bookk.feature.booking.domain.api.GetSentBooking
import me.bookk.feature.booking.domain.api.entity.SentBooking
import me.bookk.feature.booking.domain.api.entity.SentBooking.BookedService
import me.bookk.feature.booking.domain.api.entity.SentBookingRequest
import me.bookk.feature.booking.presentation.FakeBookingStateFactory
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.time.Instant
import kotlin.uuid.Uuid

class BookingSentViewModelTest {

    private val dispatchers = ViewModelTestDispatchers()

    @BeforeTest
    fun setUp() {
        dispatchers.install()
    }

    @AfterTest
    fun tearDown() {
        dispatchers.uninstall()
    }

    private val businessId = Uuid.random()
    private val request = SentBookingRequest(
        businessId = businessId,
        specialistId = Uuid.random(),
        startsAt = Instant.parse("2026-11-24T12:30:00Z"),
    )
    private val sentBooking = SentBooking(
        id = Uuid.random(),
        startsAt = LocalDateTime(2026, 11, 24, 14, 30),
        endsAt = LocalDateTime(2026, 11, 24, 15, 35),
        services = listOf(BookedService("Haircut", 1), BookedService("Beard trim", 2)),
        status = SentBooking.Status.WAITING_FOR_CONFIRMATION,
    )

    private inner class Fixture {
        val booking = MutableStateFlow<SentBooking?>(sentBooking)
        val getSentBooking = mock<GetSentBooking> {
            every { flow(request) } returns booking
        }
        val errorMapper = FakeErrorMapper()

        fun sut(): BookingSentViewModel {
            return BookingSentViewModel(
                request = request,
                getSentBooking = getSentBooking,
                dateLocalizer = FakeDateLocalizer(),
                stateFactory = FakeBookingStateFactory(),
                vmArgs = VmArgs(errorMapper),
            )
        }
    }

    @Test
    fun `shows the day of the sent request`() = runUnitTest {
        given()
        val fixture = Fixture()

        whenn()
        val state = fixture.sut().uiState

        then()
        assertEquals("2026-11-24".desc(), state.booking?.day)
    }

    @Test
    fun `shows the time and services of the sent request`() = runUnitTest {
        given()
        val fixture = Fixture()

        whenn()
        val state = fixture.sut().uiState

        then()
        val strings = BookingRes.strings
        val services = listOf(
            "Haircut".desc(),
            strings.booking_review_service_count.format("Beard trim", 2),
        ).joinToStringDesc(", ")
        assertEquals(
            strings.booking_review_details.format(
                strings.booking_review_time_range.format("14:30", "15:35"),
                services,
            ),
            state.booking?.details,
        )
    }

    @Test
    fun `shows the request waiting for confirmation`() = runUnitTest {
        given()
        val fixture = Fixture()

        whenn()
        val state = fixture.sut().uiState

        then()
        assertEquals(BookingRes.strings.booking_sent_status_waiting.desc(), state.booking?.status)
        assertEquals(ColorToken.SecondaryText, state.booking?.statusColor)
    }

    @Test
    fun `shows the request confirmed once the business confirms it`() = runUnitTest {
        given()
        val fixture = Fixture()
        val state = fixture.sut().uiState

        whenn()
        fixture.booking.value = sentBooking.copy(status = SentBooking.Status.CONFIRMED)

        then()
        assertEquals(BookingRes.strings.booking_sent_status_confirmed.desc(), state.booking?.status)
        assertEquals(ColorToken.Success, state.booking?.statusColor)
    }

    @Test
    fun `shows no request card until the sent request is cached`() = runUnitTest {
        given()
        val fixture = Fixture()
        fixture.booking.value = null

        whenn()
        val state = fixture.sut().uiState

        then()
        assertNull(state.booking)
    }

    @Test
    fun `shows the error when the sent request cannot be read`() = runUnitTest {
        given()
        val fixture = Fixture()
        every { fixture.getSentBooking.flow(request) } returns failOnceThenSuspend(TestException())

        whenn()
        fixture.sut()

        then()
        fixture.errorMapper.assertMappedSingle(TestException::class)
    }

    @Test
    fun `opens the booking details when the request is tapped`() = runUnitTest {
        given()
        val fixture = Fixture()
        val state = fixture.sut().uiState

        whenn()
        state.booking?.onClick?.invoke()

        then()
        assertEquals(
            listOf<BookingSentDestinations>(BookingSentDestinations.BookingDetails(sentBooking.id)),
            state.navigation.navigationDestination,
        )
    }

    @Test
    fun `labels the main button done`() = runUnitTest {
        given()
        val fixture = Fixture()

        whenn()
        val state = fixture.sut().uiState

        then()
        assertEquals(BookingRes.strings.booking_sent_done.desc(), state.doneButton.text)
    }

    @Test
    fun `returns home when done is tapped`() = runUnitTest {
        given()
        val fixture = Fixture()
        val state = fixture.sut().uiState

        whenn()
        state.doneButton.onClick?.invoke()

        then()
        assertEquals(
            listOf<BookingSentDestinations>(BookingSentDestinations.Home),
            state.navigation.navigationDestination,
        )
    }

    @Test
    fun `returns home on back instead of to the review`() = runUnitTest {
        given()
        val fixture = Fixture()
        val state = fixture.sut().uiState

        whenn()
        state.appBar.onBackClick?.invoke()

        then()
        assertEquals(
            listOf<BookingSentDestinations>(BookingSentDestinations.Home),
            state.navigation.navigationDestination,
        )
    }

    @Test
    fun `opens the same business's services to book something else`() = runUnitTest {
        given()
        val fixture = Fixture()
        val state = fixture.sut().uiState

        whenn()
        state.bookAgainButton.onClick?.invoke()

        then()
        assertEquals(BookingRes.strings.booking_sent_book_again.desc(), state.bookAgainButton.text)
        assertEquals(
            listOf<BookingSentDestinations>(BookingSentDestinations.Services(businessId)),
            state.navigation.navigationDestination,
        )
    }
}
