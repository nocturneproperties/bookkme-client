package me.bookk.feature.booking.presentation.screen.details

import dev.icerock.moko.resources.desc.desc
import dev.icerock.moko.resources.format
import dev.mokkery.answering.calls
import dev.mokkery.answering.returns
import dev.mokkery.answering.throws
import dev.mokkery.every
import dev.mokkery.everySuspend
import dev.mokkery.matcher.any
import dev.mokkery.mock
import dev.mokkery.verify.VerifyMode
import dev.mokkery.verifySuspend
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.datetime.LocalDateTime
import library.money.api.Money
import me.bookk.android.feature.booking.resources.BookingRes
import me.bookk.core.presentation.VmArgs
import me.bookk.core.presentation.error.ActionType
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
import me.bookk.designsystem.test.assertEmpty
import me.bookk.designsystem.test.assertMappedSingle
import me.bookk.designsystem.test.assertSingle
import me.bookk.designsystem.test.failOnceThenSuspend
import me.bookk.designsystem.test.tap
import me.bookk.feature.booking.domain.api.GetBookingRequestDetails
import me.bookk.feature.booking.domain.api.WithdrawBookingRequest
import me.bookk.feature.booking.domain.api.entity.BookingRequestDetails
import me.bookk.feature.booking.domain.api.entity.BookingSummary
import me.bookk.feature.booking.domain.api.entity.BookingSummary.BookedService
import me.bookk.feature.booking.presentation.FakeBookingStateFactory
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue
import kotlin.uuid.Uuid

class BookingRequestDetailsViewModelTest {

    private val dispatchers = ViewModelTestDispatchers()

    @BeforeTest
    fun setUp() {
        dispatchers.install()
    }

    @AfterTest
    fun tearDown() {
        dispatchers.uninstall()
    }

    private val bookingId = Uuid.random()
    private val summary = BookingSummary(
        businessName = "Nail Room",
        specialistName = "Maria Shevchuk",
        startsAt = LocalDateTime(2026, 11, 27, 11, 0),
        endsAt = LocalDateTime(2026, 11, 27, 12, 0),
        note = "Short nails, please.",
        services = listOf(
            BookedService("Manicure", 1, Money(55_000, UAH)),
            BookedService("Gel polish", 2, Money(40_000, UAH)),
        ),
        total = Money(95_000, UAH),
    )
    private val pending = BookingRequestDetails(
        id = bookingId,
        summary = summary,
        status = BookingRequestDetails.Status.WAITING_FOR_CONFIRMATION,
    )
    private val confirmed = pending.copy(status = BookingRequestDetails.Status.CONFIRMED)

    private inner class Fixture {
        val details = MutableStateFlow<BookingRequestDetails?>(pending)
        val getBookingRequestDetails = mock<GetBookingRequestDetails> {
            every { flow(bookingId) } returns details
        }
        val withdrawBookingRequest = mock<WithdrawBookingRequest> {
            everySuspend { invoke(any()) } returns Unit
        }
        val errorMapper = FakeErrorMapper()

        fun failWithdrawing(error: Throwable) {
            everySuspend { withdrawBookingRequest(any()) } throws error
        }

        fun sut(): BookingRequestDetailsViewModel {
            return BookingRequestDetailsViewModel(
                requestId = bookingId,
                getBookingRequestDetails = getBookingRequestDetails,
                withdrawBookingRequest = withdrawBookingRequest,
                dateLocalizer = FakeDateLocalizer(),
                stateFactory = FakeBookingStateFactory(),
                vmArgs = VmArgs(errorMapper),
            )
        }
    }

    private fun BookingDetailsState.withdrawalPopup(): PresentationNotification.Message {
        actionButton.onClick?.invoke()
        val popup = notifications.assertSingle<PresentationNotification.Message>()
        notifications.removeFirst()
        return popup
    }

    private fun BookingDetailsState.withdrawConfirmed() {
        withdrawalPopup().tap(ActionType.NEGATIVE)
    }

    @Test
    fun `shows the business of the booking`() = runUnitTest {
        given()
        val fixture = Fixture()

        whenn()
        val state = fixture.sut().uiState

        then()
        assertEquals("Nail Room".desc(), state.booking?.businessName)
    }

    @Test
    fun `shows a pending request waiting for confirmation`() = runUnitTest {
        given()
        val fixture = Fixture()

        whenn()
        val state = fixture.sut().uiState

        then()
        assertEquals(BookingRes.strings.booking_sent_status_waiting.desc(), state.booking?.status)
        assertEquals(ColorToken.SecondaryText, state.booking?.statusColor)
    }

    @Test
    fun `shows an approved request confirmed`() = runUnitTest {
        given()
        val fixture = Fixture()
        fixture.details.value = confirmed

        whenn()
        val state = fixture.sut().uiState

        then()
        assertEquals(BookingRes.strings.booking_sent_status_confirmed.desc(), state.booking?.status)
        assertEquals(ColorToken.Success, state.booking?.statusColor)
    }

    @Test
    fun `shows when, with whom and the client's note`() = runUnitTest {
        given()
        val fixture = Fixture()

        whenn()
        val state = fixture.sut().uiState

        then()
        val strings = BookingRes.strings
        assertEquals(
            listOf(
                BookingDetailsRow(
                    strings.booking_details_when.desc(),
                    strings.booking_details_when_value.format(
                        "2026-11-27",
                        strings.booking_review_time_range.format("11:00", "12:00"),
                    ),
                ),
                BookingDetailsRow(strings.booking_details_with.desc(), "Maria Shevchuk".desc()),
                BookingDetailsRow(
                    strings.booking_details_note.desc(),
                    "Short nails, please.".desc(),
                ),
            ),
            state.booking?.rows,
        )
    }

    @Test
    fun `leaves the note out when the client wrote none`() = runUnitTest {
        given()
        val fixture = Fixture()
        fixture.details.value = pending.copy(summary = summary.copy(note = "  "))

        whenn()
        val state = fixture.sut().uiState

        then()
        assertEquals(
            listOf(
                BookingRes.strings.booking_details_when.desc(),
                BookingRes.strings.booking_details_with.desc(),
            ),
            state.booking?.rows?.map { it.title },
        )
    }

    @Test
    fun `lists each booked service with its price and the count when repeated`() = runUnitTest {
        given()
        val fixture = Fixture()

        whenn()
        val state = fixture.sut().uiState

        then()
        assertEquals(
            listOf(
                BookingDetailsServiceItem("Manicure".desc(), Money(55_000, UAH).toString().desc()),
                BookingDetailsServiceItem(
                    BookingRes.strings.booking_review_service_count.format("Gel polish", 2),
                    Money(40_000, UAH).toString().desc(),
                ),
            ),
            state.booking?.services,
        )
    }

    @Test
    fun `shows the total price of the booking`() = runUnitTest {
        given()
        val fixture = Fixture()

        whenn()
        val state = fixture.sut().uiState

        then()
        assertEquals(Money(95_000, UAH).toString().desc(), state.booking?.total)
    }

    @Test
    fun `explains that the business has not answered a pending request yet`() = runUnitTest {
        given()
        val fixture = Fixture()

        whenn()
        val state = fixture.sut().uiState

        then()
        assertEquals(
            BookingRes.strings.booking_details_waiting_explanation.format("Nail Room"),
            state.booking?.statusExplanation,
        )
    }

    @Test
    fun `explains that the business approved an approved request`() = runUnitTest {
        given()
        val fixture = Fixture()
        fixture.details.value = confirmed

        whenn()
        val state = fixture.sut().uiState

        then()
        assertEquals(
            BookingRes.strings.booking_details_confirmed_explanation.format("Nail Room"),
            state.booking?.statusExplanation,
        )
    }

    @Test
    fun `shows a declined request declined by the business`() = runUnitTest {
        given()
        val fixture = Fixture()
        fixture.details.value = pending.copy(status = BookingRequestDetails.Status.DECLINED)

        whenn()
        val state = fixture.sut().uiState

        then()
        assertEquals(
            BookingRes.strings.booking_details_status_declined.desc(),
            state.booking?.status,
        )
        assertEquals(ColorToken.Error, state.booking?.statusColor)
        assertEquals(
            BookingRes.strings.booking_details_declined_explanation.format("Nail Room"),
            state.booking?.statusExplanation,
        )
    }

    @Test
    fun `shows a withdrawn or expired request closed`() = runUnitTest {
        given()
        val fixture = Fixture()
        fixture.details.value = pending.copy(status = BookingRequestDetails.Status.CLOSED)

        whenn()
        val state = fixture.sut().uiState

        then()
        assertEquals(BookingRes.strings.booking_details_status_closed.desc(), state.booking?.status)
        assertEquals(ColorToken.SecondaryText, state.booking?.statusColor)
        assertEquals(
            BookingRes.strings.booking_details_closed_explanation.desc(),
            state.booking?.statusExplanation,
        )
    }

    @Test
    fun `offers to withdraw a pending request`() = runUnitTest {
        given()
        val fixture = Fixture()

        whenn()
        val state = fixture.sut().uiState

        then()
        assertTrue(state.actionButton.isVisible)
        assertEquals(BookingRes.strings.booking_details_withdraw.desc(), state.actionButton.text)
    }

    @Test
    fun `does not offer to withdraw a request once it is answered`() = runUnitTest {
        given()
        val fixture = Fixture()
        val state = fixture.sut().uiState

        whenn()
        fixture.details.value = confirmed

        then()
        assertFalse(state.actionButton.isVisible)
    }

    @Test
    fun `goes back once the request is gone`() = runUnitTest {
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
        assertFalse(state.actionButton.isVisible)
    }

    @Test
    fun `goes back only once when the answered request disappears while withdrawing`() =
        runUnitTest {
            given()
            val fixture = Fixture()
            everySuspend { fixture.withdrawBookingRequest(bookingId) } calls {
                fixture.details.value = null
                throw WithdrawBookingRequest.Error.AlreadyConfirmed()
            }
            val state = fixture.sut().uiState

            whenn()
            state.withdrawConfirmed()

            then()
            assertEquals(
                listOf<BookingDetailsDestinations>(BookingDetailsDestinations.Back),
                state.navigation.navigationDestination,
            )
        }

    @Test
    fun `shows a failed request load once`() = runUnitTest {
        given()
        val fixture = Fixture()
        every { fixture.getBookingRequestDetails.flow(bookingId) } returns
            failOnceThenSuspend(TestException())

        whenn()
        val state = fixture.sut().uiState

        then()
        state.notifications.assertSingle<PresentationNotification.GlobalMessage>()
        fixture.errorMapper.assertMappedSingle(TestException::class)
    }

    @Test
    fun `asks before withdrawing the request`() = runUnitTest {
        given()
        val fixture = Fixture()
        val state = fixture.sut().uiState

        whenn()
        val popup = state.withdrawalPopup()

        then()
        assertEquals(BookingRes.strings.booking_details_withdraw_title.desc(), popup.title)
        assertEquals(
            BookingRes.strings.booking_details_withdraw_message.format("Nail Room"),
            popup.message,
        )
        assertEquals(
            listOf(
                BookingRes.strings.booking_details_withdraw_keep.desc() to ActionType.CANCEL,
                BookingRes.strings.booking_details_withdraw_confirm.desc() to ActionType.NEGATIVE,
            ),
            popup.buttons.map { it.text to it.actionType },
        )
        verifySuspend(VerifyMode.not) { fixture.withdrawBookingRequest(any()) }
    }

    @Test
    fun `keeps the request when the client keeps it`() = runUnitTest {
        given()
        val fixture = Fixture()
        val state = fixture.sut().uiState

        whenn()
        state.withdrawalPopup().tap(ActionType.CANCEL)

        then()
        verifySuspend(VerifyMode.not) { fixture.withdrawBookingRequest(any()) }
        assertTrue(state.navigation.navigationDestination.isEmpty())
    }

    @Test
    fun `withdraws the request once the client confirms`() = runUnitTest {
        given()
        val fixture = Fixture()
        val state = fixture.sut().uiState

        whenn()
        state.withdrawConfirmed()

        then()
        verifySuspend { fixture.withdrawBookingRequest(bookingId) }
    }

    @Test
    fun `returns to the bookings once the request is withdrawn`() = runUnitTest {
        given()
        val fixture = Fixture()
        val state = fixture.sut().uiState

        whenn()
        state.withdrawConfirmed()

        then()
        assertEquals(
            listOf<BookingDetailsDestinations>(BookingDetailsDestinations.Bookings),
            state.navigation.navigationDestination,
        )
    }

    @Test
    fun `plays success haptic when the request is withdrawn`() = runUnitTest {
        given()
        val fixture = Fixture()
        val state = fixture.sut().uiState

        whenn()
        state.withdrawConfirmed()

        then()
        state.notifications.assertSingle<PresentationNotification.SuccessHaptic>()
    }

    @Test
    fun `does not play success haptic when withdrawing fails`() = runUnitTest {
        given()
        val fixture = Fixture()
        fixture.failWithdrawing(TestException())
        val state = fixture.sut().uiState

        whenn()
        state.withdrawConfirmed()

        then()
        assertTrue(
            state.notifications.presentationNotification.none {
                it == PresentationNotification.SuccessHaptic
            },
        )
    }

    @Test
    fun `shows the button loading while withdrawing`() = runUnitTest {
        given()
        val fixture = Fixture()
        val withdrawing = CompletableDeferred<Unit>()
        everySuspend { fixture.withdrawBookingRequest(bookingId) } calls { withdrawing.await() }
        val state = fixture.sut().uiState

        whenn()
        state.withdrawConfirmed()

        then()
        assertTrue(state.actionButton.isLoading)
        withdrawing.complete(Unit)
    }

    @Test
    fun `stops the button loading once withdrawing fails`() = runUnitTest {
        given()
        val fixture = Fixture()
        fixture.failWithdrawing(TestException())
        val state = fixture.sut().uiState

        whenn()
        state.withdrawConfirmed()

        then()
        assertFalse(state.actionButton.isLoading)
        assertTrue(state.actionButton.isEnabled)
    }

    @Test
    fun `shows an unknown withdrawing failure once and stays on the booking`() = runUnitTest {
        given()
        val fixture = Fixture()
        fixture.failWithdrawing(TestException())
        val state = fixture.sut().uiState

        whenn()
        state.withdrawConfirmed()

        then()
        state.notifications.assertSingle<PresentationNotification.GlobalMessage>()
        fixture.errorMapper.assertMappedSingle(TestException::class)
        assertTrue(state.navigation.navigationDestination.isEmpty())
    }

    @Test
    fun `says the business already confirmed the booking and goes back`() = runUnitTest {
        given()
        val fixture = Fixture()
        fixture.failWithdrawing(WithdrawBookingRequest.Error.AlreadyConfirmed())
        val state = fixture.sut().uiState

        whenn()
        state.withdrawConfirmed()

        then()
        val shown = state.notifications.assertSingle<PresentationNotification.Message>()
        assertEquals(BookingRes.strings.booking_details_already_confirmed_title.desc(), shown.title)
        assertEquals(
            BookingRes.strings.booking_details_already_confirmed_message.format("Nail Room"),
            shown.message,
        )
        assertEquals(
            listOf<BookingDetailsDestinations>(BookingDetailsDestinations.Back),
            state.navigation.navigationDestination,
        )
    }

    @Test
    fun `says the request is no longer pending and goes back`() = runUnitTest {
        given()
        val fixture = Fixture()
        fixture.failWithdrawing(WithdrawBookingRequest.Error.NoLongerPending())
        val state = fixture.sut().uiState

        whenn()
        state.withdrawConfirmed()

        then()
        val shown = state.notifications.assertSingle<PresentationNotification.Message>()
        assertEquals(BookingRes.strings.booking_details_no_longer_pending_title.desc(), shown.title)
        assertEquals(
            BookingRes.strings.booking_details_no_longer_pending_message.desc(),
            shown.message,
        )
        assertEquals(
            listOf<BookingDetailsDestinations>(BookingDetailsDestinations.Back),
            state.navigation.navigationDestination,
        )
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
        state.notifications.assertEmpty()
    }

    private companion object {
        val UAH = Money.SupportedCurrency.UAH
    }
}
