package me.bookk.feature.booking.presentation.screen.details

import dev.icerock.moko.resources.desc.StringDesc
import dev.icerock.moko.resources.desc.desc
import dev.icerock.moko.resources.format
import dev.mokkery.answering.returns
import dev.mokkery.every
import dev.mokkery.mock
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.datetime.LocalDateTime
import library.money.api.Money
import me.bookk.android.feature.booking.resources.BookingRes
import me.bookk.core.presentation.VmArgs
import me.bookk.core.presentation.error.PresentationNotification
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
import me.bookk.designsystem.test.assertSingle
import me.bookk.designsystem.test.failOnceThenSuspend
import me.bookk.feature.booking.domain.api.GetBookingAppointmentDetails
import me.bookk.feature.booking.domain.api.entity.BookingAppointmentDetails
import me.bookk.feature.booking.domain.api.entity.BookingAppointmentDetails.Status
import me.bookk.feature.booking.domain.api.entity.BookingSummary
import me.bookk.feature.booking.domain.api.entity.BookingSummary.BookedService
import me.bookk.feature.booking.presentation.FakeBookingStateFactory
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.uuid.Uuid

class BookingAppointmentDetailsViewModelTest {

    private val dispatchers = ViewModelTestDispatchers()

    @BeforeTest
    fun setUp() {
        dispatchers.install()
    }

    @AfterTest
    fun tearDown() {
        dispatchers.uninstall()
    }

    private val appointmentId = Uuid.random()
    private val confirmed = BookingAppointmentDetails(
        id = appointmentId,
        summary = BookingSummary(
            businessName = "Studio Lumen",
            specialistName = "Anna Kovalenko",
            startsAt = LocalDateTime(2026, 11, 24, 14, 30),
            endsAt = LocalDateTime(2026, 11, 24, 15, 35),
            note = "Short at the back.",
            services = listOf(BookedService("Haircut", 1, Money(60_000, UAH))),
            total = Money(60_000, UAH),
        ),
        status = Status.CONFIRMED,
    )

    private inner class Fixture {
        val details = MutableStateFlow<BookingAppointmentDetails?>(confirmed)
        val getBookingAppointmentDetails = mock<GetBookingAppointmentDetails> {
            every { flow(appointmentId) } returns details
        }
        val errorMapper = FakeErrorMapper()

        fun sut(): BookingAppointmentDetailsViewModel {
            return BookingAppointmentDetailsViewModel(
                appointmentId = appointmentId,
                getBookingAppointmentDetails = getBookingAppointmentDetails,
                dateLocalizer = FakeDateLocalizer(),
                stateFactory = FakeBookingStateFactory(),
                vmArgs = VmArgs(errorMapper),
            )
        }
    }

    @Test
    fun `shows the business, when, with whom and the client's note`() = runUnitTest {
        given()
        val fixture = Fixture()

        whenn()
        val state = fixture.sut().uiState

        then()
        val strings = BookingRes.strings
        assertEquals("Studio Lumen".desc(), state.booking?.businessName)
        assertEquals(
            listOf(
                BookingDetailsRow(
                    strings.booking_details_when.desc(),
                    strings.booking_details_when_value.format(
                        "2026-11-24",
                        strings.booking_review_time_range.format("14:30", "15:35"),
                    ),
                ),
                BookingDetailsRow(strings.booking_details_with.desc(), "Anna Kovalenko".desc()),
                BookingDetailsRow(strings.booking_details_note.desc(), "Short at the back.".desc()),
            ),
            state.booking?.rows,
        )
    }

    @Test
    fun `shows the booked services and their total`() = runUnitTest {
        given()
        val fixture = Fixture()

        whenn()
        val state = fixture.sut().uiState

        then()
        val price = Money(60_000, UAH).toString().desc()
        assertEquals(
            listOf(BookingDetailsServiceItem("Haircut".desc(), price)),
            state.booking?.services,
        )
        assertEquals(price, state.booking?.total)
    }

    @Test
    fun `shows a scheduled appointment confirmed`() = runUnitTest {
        given()
        val fixture = Fixture()

        whenn()
        val state = fixture.sut().uiState

        then()
        assertStatus(
            state,
            BookingRes.strings.booking_sent_status_confirmed.desc(),
            ColorToken.Success,
            BookingRes.strings.booking_details_confirmed_explanation.format("Studio Lumen"),
        )
    }

    @Test
    fun `shows a past appointment done`() = runUnitTest {
        given()
        val fixture = Fixture()
        fixture.details.value = confirmed.copy(status = Status.COMPLETED)

        whenn()
        val state = fixture.sut().uiState

        then()
        assertStatus(
            state,
            BookingRes.strings.booking_details_status_done.desc(),
            ColorToken.SecondaryText,
            BookingRes.strings.booking_details_done_explanation.desc(),
        )
    }

    @Test
    fun `shows a cancelled appointment cancelled`() = runUnitTest {
        given()
        val fixture = Fixture()
        fixture.details.value = confirmed.copy(status = Status.CANCELLED)

        whenn()
        val state = fixture.sut().uiState

        then()
        assertStatus(
            state,
            BookingRes.strings.booking_details_status_cancelled.desc(),
            ColorToken.Error,
            BookingRes.strings.booking_details_cancelled_explanation.desc(),
        )
    }

    @Test
    fun `shows a no-show appointment missed`() = runUnitTest {
        given()
        val fixture = Fixture()
        fixture.details.value = confirmed.copy(status = Status.MISSED)

        whenn()
        val state = fixture.sut().uiState

        then()
        assertStatus(
            state,
            BookingRes.strings.booking_details_status_missed.desc(),
            ColorToken.Error,
            BookingRes.strings.booking_details_missed_explanation.desc(),
        )
    }

    @Test
    fun `offers no action on an appointment`() = runUnitTest {
        given()
        val fixture = Fixture()

        whenn()
        val state = fixture.sut().uiState

        then()
        assertFalse(state.actionButton.isVisible)
    }

    @Test
    fun `goes back once the appointment is gone`() = runUnitTest {
        given()
        val fixture = Fixture()
        val state = fixture.sut().uiState

        whenn()
        fixture.details.value = null

        then()
        assertEquals(
            listOf<BookingDetailsDestinations>(BookingDetailsDestinations.Back),
            state.navigation.navigationDestination,
        )
    }

    @Test
    fun `shows a failed appointment load once`() = runUnitTest {
        given()
        val fixture = Fixture()
        every { fixture.getBookingAppointmentDetails.flow(appointmentId) } returns
            failOnceThenSuspend(TestException())

        whenn()
        val state = fixture.sut().uiState

        then()
        state.notifications.assertSingle<PresentationNotification.GlobalMessage>()
        fixture.errorMapper.assertMappedSingle(TestException::class)
    }

    @Test
    fun `goes back from the native back button`() = runUnitTest {
        given()
        val fixture = Fixture()
        val state = fixture.sut().uiState

        whenn()
        state.appBar.onBackClick?.invoke()

        then()
        assertEquals(
            listOf<BookingDetailsDestinations>(BookingDetailsDestinations.Back),
            state.navigation.navigationDestination,
        )
    }

    private fun assertStatus(
        state: BookingDetailsState,
        label: StringDesc,
        color: ColorToken,
        explanation: StringDesc,
    ) {
        assertEquals(label, state.booking?.status)
        assertEquals(color, state.booking?.statusColor)
        assertEquals(explanation, state.booking?.statusExplanation)
    }

    private companion object {
        val UAH = Money.SupportedCurrency.UAH
    }
}
