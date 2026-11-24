package me.bookk.feature.booking.presentation.screen.review

import dev.icerock.moko.resources.desc.desc
import dev.icerock.moko.resources.format
import dev.mokkery.answering.returns
import dev.mokkery.answering.throws
import dev.mokkery.every
import dev.mokkery.everySuspend
import dev.mokkery.matcher.any
import dev.mokkery.mock
import dev.mokkery.verifySuspend
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atTime
import kotlinx.datetime.toInstant
import library.money.api.Money
import me.bookk.android.feature.booking.resources.BookingRes
import me.bookk.core.presentation.VmArgs
import me.bookk.core.presentation.error.PresentationNotification
import me.bookk.core.test.given
import me.bookk.core.test.runUnitTest
import me.bookk.core.test.then
import me.bookk.core.test.whenn
import me.bookk.designsystem.test.FakeDateLocalizer
import me.bookk.designsystem.test.FakeErrorMapper
import me.bookk.designsystem.test.FakeTextFieldState
import me.bookk.designsystem.test.TestException
import me.bookk.designsystem.test.ViewModelTestDispatchers
import me.bookk.designsystem.test.assertMappedSingle
import me.bookk.designsystem.test.assertNothingMapped
import me.bookk.designsystem.test.assertSingle
import me.bookk.designsystem.test.failOnceThenSuspend
import me.bookk.feature.booking.domain.api.GetBookingPreparation
import me.bookk.feature.booking.domain.api.GetBookingTimetable
import me.bookk.feature.booking.domain.api.GetBusinessMessage
import me.bookk.feature.booking.domain.api.SendBookingRequest
import me.bookk.feature.booking.domain.api.entity.BookingPreparation
import me.bookk.feature.booking.domain.api.entity.BookingPreparation.PreparedService
import me.bookk.feature.booking.domain.api.entity.BookingService
import me.bookk.feature.booking.domain.api.entity.BookingSpecialist
import me.bookk.feature.booking.domain.api.entity.BookingTimetable
import me.bookk.feature.booking.domain.api.entity.SentBookingRequest
import me.bookk.feature.booking.presentation.FakeBookingStateFactory
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertTrue
import kotlin.time.Duration.Companion.minutes
import kotlin.uuid.Uuid

class BookingReviewViewModelTest {

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
    private val kyiv = TimeZone.of("Europe/Kyiv")
    private val day = LocalDate(2026, 11, 24)
    private val slotStart = day.atTime(LocalTime(14, 30)).toInstant(kyiv)
    private val haircut = BookingService(
        id = Uuid.random(),
        name = "Haircut",
        duration = 45.minutes,
        price = Money(60_000, Money.SupportedCurrency.UAH),
    )
    private val beardTrim = BookingService(
        id = Uuid.random(),
        name = "Beard trim",
        duration = 10.minutes,
        price = Money(15_000, Money.SupportedCurrency.UAH),
    )
    private val andrii = BookingSpecialist(Uuid.random(), "Andrii", "Koval")
    private val studio = BookingTimetable("Studio Lumen", kyiv, andrii)

    private inner class Fixture {
        val timetable = MutableStateFlow<BookingTimetable?>(studio)
        val preparation = MutableStateFlow(
            BookingPreparation(
                services = listOf(PreparedService(haircut, 1), PreparedService(beardTrim, 2)),
                specialistId = andrii.id,
                slotStart = slotStart,
            ),
        )
        val getBookingTimetable = mock<GetBookingTimetable> {
            every { flow(businessId) } returns timetable.filterNotNull()
        }
        val getBookingPreparation = mock<GetBookingPreparation> {
            every { flow(businessId) } returns preparation
        }
        val message = MutableStateFlow<String?>("Please come 5 minutes early.")
        val getBusinessMessage = mock<GetBusinessMessage> {
            every { flow(businessId) } returns message
            everySuspend { refresh(businessId) } returns Unit
        }
        val sentRequest = SentBookingRequest(businessId, andrii.id, slotStart)
        val sendBookingRequest = mock<SendBookingRequest> {
            everySuspend { invoke(any(), any()) } returns sentRequest
        }
        val errorMapper = FakeErrorMapper()

        fun sut(): BookingReviewViewModel {
            return BookingReviewViewModel(
                businessId = businessId,
                getBookingTimetable = getBookingTimetable,
                getBookingPreparation = getBookingPreparation,
                getBusinessMessage = getBusinessMessage,
                sendBookingRequest = sendBookingRequest,
                dateLocalizer = FakeDateLocalizer(),
                stateFactory = FakeBookingStateFactory(),
                vmArgs = VmArgs(errorMapper),
            )
        }

        fun failSending(error: Throwable) {
            everySuspend { sendBookingRequest(any(), any()) } throws error
        }
    }

    private fun BookingReviewState.sendTapped() {
        sendButton.onClick?.invoke()
    }

    @Test
    fun `shows the business name over the title`() = runUnitTest {
        given()
        val fixture = Fixture()

        whenn()
        val state = fixture.sut().uiState

        then()
        assertEquals("Studio Lumen".desc(), state.businessName)
    }

    @Test
    fun `shows the picked day`() = runUnitTest {
        given()
        val fixture = Fixture()

        whenn()
        val state = fixture.sut().uiState

        then()
        assertEquals(day.toString().desc(), state.summary?.day)
    }

    @Test
    fun `shows the time range with the specialist`() = runUnitTest {
        given()
        val fixture = Fixture()

        whenn()
        val state = fixture.sut().uiState

        then()
        assertEquals(
            BookingRes.strings.booking_review_details.format(
                BookingRes.strings.booking_review_time_range.format("14:30", "15:35"),
                "Andrii Koval",
            ),
            state.summary?.details,
        )
    }

    @Test
    fun `shows only the time range when the specialist is unknown`() = runUnitTest {
        given()
        val fixture = Fixture()
        fixture.timetable.value = studio.copy(specialist = null)

        whenn()
        val state = fixture.sut().uiState

        then()
        assertEquals(
            BookingRes.strings.booking_review_time_range.format("14:30", "15:35"),
            state.summary?.details,
        )
    }

    @Test
    fun `lists each picked service with its price and the count when repeated`() = runUnitTest {
        given()
        val fixture = Fixture()

        whenn()
        val state = fixture.sut().uiState

        then()
        val services = assertNotNull(state.summary).services
        assertEquals(
            listOf(
                "Haircut".desc(),
                BookingRes.strings.booking_review_service_count.format("Beard trim", 2),
            ),
            services.map { it.name },
        )
        assertEquals(
            listOf(haircut.price.toString().desc(), (beardTrim.price * 2).toString().desc()),
            services.map { it.price },
        )
    }

    @Test
    fun `shows no previous price for services at the price they were approved at`() = runUnitTest {
        given()
        val fixture = Fixture()

        whenn()
        val state = fixture.sut().uiState

        then()
        assertEquals(
            listOf(null, null),
            assertNotNull(state.summary).services.map { it.previousPrice },
        )
    }

    @Test
    fun `shows the new price beside the approved one when a service was repriced`() = runUnitTest {
        given()
        val fixture = Fixture()
        val raised = beardTrim.copy(price = Money(20_000, Money.SupportedCurrency.UAH))
        fixture.preparation.value = fixture.preparation.value.copy(
            services = listOf(
                PreparedService(haircut, 1),
                PreparedService(raised, 2, approvedPrice = beardTrim.price),
            ),
        )

        whenn()
        val state = fixture.sut().uiState

        then()
        val repriced = assertNotNull(state.summary).services.last()
        assertEquals((beardTrim.price * 2).toString().desc(), repriced.previousPrice)
        assertEquals((raised.price * 2).toString().desc(), repriced.price)
    }

    @Test
    fun `shows the total price of the picked services`() = runUnitTest {
        given()
        val fixture = Fixture()

        whenn()
        val state = fixture.sut().uiState

        then()
        assertEquals(
            (haircut.price + beardTrim.price * 2).toString().desc(),
            state.summary?.total,
        )
    }

    @Test
    fun `says the business confirms the request`() = runUnitTest {
        given()
        val fixture = Fixture()

        whenn()
        val state = fixture.sut().uiState

        then()
        assertEquals(
            BookingRes.strings.booking_review_confirmation_notice.format("Studio Lumen"),
            state.confirmationNotice,
        )
    }

    @Test
    fun `shows the business's message to clients under its name`() = runUnitTest {
        given()
        val fixture = Fixture()

        whenn()
        val state = fixture.sut().uiState

        then()
        assertEquals(
            BusinessMessage(
                title = BookingRes.strings.booking_review_business_message_title
                    .format("Studio Lumen"),
                text = "Please come 5 minutes early.".desc(),
            ),
            state.businessMessage,
        )
    }

    @Test
    fun `shows no message when the business wrote none`() = runUnitTest {
        given()
        val fixture = Fixture()
        fixture.message.value = null

        whenn()
        val state = fixture.sut().uiState

        then()
        assertEquals(null, state.businessMessage)
    }

    @Test
    fun `refreshes the business's message when the screen opens`() = runUnitTest {
        given()
        val fixture = Fixture()

        whenn()
        fixture.sut()

        then()
        verifySuspend { fixture.getBusinessMessage.refresh(businessId) }
    }

    @Test
    fun `keeps the cached message without a popup when its refresh fails`() = runUnitTest {
        given()
        val fixture = Fixture()
        everySuspend { fixture.getBusinessMessage.refresh(businessId) } throws TestException()

        whenn()
        val state = fixture.sut().uiState

        then()
        assertNotNull(state.businessMessage)
        assertTrue(state.notifications.presentationNotification.isEmpty())
        fixture.errorMapper.assertNothingMapped()
    }

    @Test
    fun `shows a failed message load once`() = runUnitTest {
        given()
        val fixture = Fixture()
        every { fixture.getBusinessMessage.flow(businessId) } returns failOnceThenSuspend()

        whenn()
        val state = fixture.sut().uiState

        then()
        fixture.errorMapper.assertMappedSingle(TestException::class)
        state.notifications.assertSingle<PresentationNotification.GlobalMessage>()
    }

    @Test
    fun `leaves the booking when the business no longer takes online bookings`() = runUnitTest {
        given()
        val fixture = Fixture()
        everySuspend { fixture.getBusinessMessage.refresh(businessId) } throws
            GetBusinessMessage.Error.BookingUnavailable()

        whenn()
        val state = fixture.sut().uiState

        then()
        val shown = state.notifications.assertSingle<PresentationNotification.Message>()
        assertEquals(
            BookingRes.strings.booking_date_time_unavailable.format("Studio Lumen"),
            shown.message,
        )
        assertEquals(
            listOf<BookingReviewDestinations>(BookingReviewDestinations.Leave),
            state.navigation.navigationDestination,
        )
    }

    @Test
    fun `waits for the business before showing the request`() = runUnitTest {
        given()
        val fixture = Fixture()
        fixture.timetable.value = null

        whenn()
        val state = fixture.sut().uiState

        then()
        assertEquals(null, state.summary)
    }

    @Test
    fun `keeps the request on screen when the booking preparation is forgotten`() = runUnitTest {
        given()
        val fixture = Fixture()
        val state = fixture.sut().uiState
        val shown = state.summary

        whenn()
        fixture.preparation.value = BookingPreparation(services = emptyList())

        then()
        assertEquals(shown, state.summary)
    }

    @Test
    fun `limits the note to the length the business can store`() = runUnitTest {
        given()
        val fixture = Fixture()

        whenn()
        val state = fixture.sut().uiState

        then()
        assertEquals(BookingReviewViewModel.NOTE_MAX_LENGTH, state.note.maxLength)
    }

    @Test
    fun `sends the request with the typed note`() = runUnitTest {
        given()
        val fixture = Fixture()
        val state = fixture.sut().uiState
        (state.note as FakeTextFieldState).type("Window seat, please")

        whenn()
        state.sendTapped()

        then()
        verifySuspend { fixture.sendBookingRequest(businessId, "Window seat, please") }
    }

    @Test
    fun `opens request sent once the request is sent`() = runUnitTest {
        given()
        val fixture = Fixture()
        val state = fixture.sut().uiState

        whenn()
        state.sendTapped()

        then()
        assertEquals(
            listOf<BookingReviewDestinations>(
                BookingReviewDestinations.Sent(fixture.sentRequest),
            ),
            state.navigation.navigationDestination,
        )
    }

    @Test
    fun `plays success haptic when the request is sent`() = runUnitTest {
        given()
        val fixture = Fixture()
        val state = fixture.sut().uiState

        whenn()
        state.sendTapped()

        then()
        state.notifications.assertSingle<PresentationNotification.SuccessHaptic>()
    }

    @Test
    fun `does not play success haptic when sending fails`() = runUnitTest {
        given()
        val fixture = Fixture()
        fixture.failSending(TestException())
        val state = fixture.sut().uiState

        whenn()
        state.sendTapped()

        then()
        assertTrue(
            state.notifications.presentationNotification.none {
                it == PresentationNotification.SuccessHaptic
            },
        )
    }

    @Test
    fun `stops the button loading once sending ends`() = runUnitTest {
        given()
        val fixture = Fixture()
        fixture.failSending(TestException())
        val state = fixture.sut().uiState

        whenn()
        state.sendTapped()

        then()
        assertFalse(state.sendButton.isLoading)
        assertTrue(state.sendButton.isEnabled)
    }

    @Test
    fun `shows an unknown sending failure once and stays on the step`() = runUnitTest {
        given()
        val fixture = Fixture()
        fixture.failSending(TestException())
        val state = fixture.sut().uiState

        whenn()
        state.sendTapped()

        then()
        fixture.errorMapper.assertMappedSingle(TestException::class)
        state.notifications.assertSingle<PresentationNotification.GlobalMessage>()
        assertTrue(state.navigation.navigationDestination.isEmpty())
    }

    @Test
    fun `says the time is no longer free when it was taken`() = runUnitTest {
        assertSendErrorShown(
            SendBookingRequest.Error.TimeUnavailable(),
            PresentationNotification.Message(
                title = BookingRes.strings.booking_review_time_unavailable_title.desc(),
                message = BookingRes.strings.booking_review_time_unavailable_message.desc(),
                buttons = emptyList(),
            ),
        )
    }

    @Test
    fun `says the business still has to answer earlier requests at the limit`() = runUnitTest {
        assertSendErrorShown(
            SendBookingRequest.Error.PendingRequestsLimitReached(),
            PresentationNotification.Message(
                title = BookingRes.strings.booking_review_requests_limit_title.desc(),
                message = BookingRes.strings.booking_review_requests_limit_message
                    .format("Studio Lumen"),
                buttons = emptyList(),
            ),
        )
    }

    @Test
    fun `says the services changed when they no longer match`() = runUnitTest {
        assertSendErrorShown(
            SendBookingRequest.Error.ServicesChanged(),
            PresentationNotification.Message(
                title = BookingRes.strings.booking_review_services_changed_title.desc(),
                message = BookingRes.strings.booking_review_services_changed_message
                    .format("Studio Lumen"),
                buttons = emptyList(),
            ),
        )
    }

    @Test
    fun `asks to check the new prices and send again when they changed`() = runUnitTest {
        assertSendErrorShown(
            SendBookingRequest.Error.PricesChanged(),
            PresentationNotification.Message(
                title = BookingRes.strings.booking_review_prices_changed_title.desc(),
                message = BookingRes.strings.booking_review_prices_changed_message.desc(),
                buttons = emptyList(),
            ),
        )
    }

    @Test
    fun `says the specialist can no longer be booked`() = runUnitTest {
        assertSendErrorShown(
            SendBookingRequest.Error.SpecialistUnavailable(),
            PresentationNotification.Message(
                title = BookingRes.strings.booking_review_specialist_unavailable_title.desc(),
                message = BookingRes.strings.booking_review_specialist_unavailable_message.desc(),
                buttons = emptyList(),
            ),
        )
    }

    @Test
    fun `leaves the booking when the picked services, specialist or time are missing`() =
        runUnitTest {
            given()
            val fixture = Fixture()
            fixture.failSending(SendBookingRequest.Error.SelectionIncomplete())
            val state = fixture.sut().uiState

            whenn()
            state.sendTapped()

            then()
            assertEquals(
                listOf<BookingReviewDestinations>(BookingReviewDestinations.Leave),
                state.navigation.navigationDestination,
            )
            assertTrue(state.notifications.presentationNotification.isEmpty())
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
            listOf<BookingReviewDestinations>(BookingReviewDestinations.Back),
            state.navigation.navigationDestination,
        )
    }

    private suspend fun assertSendErrorShown(
        error: Throwable,
        expected: PresentationNotification.Message,
    ) {
        given()
        val fixture = Fixture()
        fixture.failSending(error)
        val state = fixture.sut().uiState

        whenn()
        state.sendTapped()

        then()
        val shown = state.notifications.assertSingle<PresentationNotification.Message>()
        assertEquals(expected.title, shown.title)
        assertEquals(expected.message, shown.message)
        assertTrue(shown.buttons.isEmpty())
        fixture.errorMapper.assertNothingMapped()
        assertTrue(state.navigation.navigationDestination.isEmpty())
    }
}
