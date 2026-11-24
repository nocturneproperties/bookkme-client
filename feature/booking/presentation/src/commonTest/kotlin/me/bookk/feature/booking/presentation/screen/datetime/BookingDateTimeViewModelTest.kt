package me.bookk.feature.booking.presentation.screen.datetime

import dev.icerock.moko.resources.desc.desc
import dev.icerock.moko.resources.format
import dev.mokkery.answering.returns
import dev.mokkery.answering.throws
import dev.mokkery.every
import dev.mokkery.everySuspend
import dev.mokkery.matcher.any
import dev.mokkery.mock
import dev.mokkery.verify.VerifyMode
import dev.mokkery.verifySuspend
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atTime
import kotlinx.datetime.plus
import kotlinx.datetime.toInstant
import library.money.api.Money
import me.bookk.android.designsystem.resources.DesignSystem
import me.bookk.android.feature.booking.resources.BookingRes
import me.bookk.core.presentation.VmArgs
import me.bookk.core.presentation.date.startOfWeek
import me.bookk.core.presentation.error.PresentationNotification
import me.bookk.core.test.given
import me.bookk.core.test.runUnitTest
import me.bookk.core.test.then
import me.bookk.core.test.whenn
import me.bookk.designsystem.test.FakeDateLocalizer
import me.bookk.designsystem.test.FakeErrorMapper
import me.bookk.designsystem.test.TestException
import me.bookk.designsystem.test.ViewModelTestDispatchers
import me.bookk.designsystem.test.assertMappedSingle
import me.bookk.designsystem.test.assertSingle
import me.bookk.feature.booking.domain.api.GetBookingPreparation
import me.bookk.feature.booking.domain.api.GetBookingSlots
import me.bookk.feature.booking.domain.api.GetBookingTimetable
import me.bookk.feature.booking.domain.api.SetBookingSlot
import me.bookk.feature.booking.domain.api.entity.BookingDay
import me.bookk.feature.booking.domain.api.entity.BookingPreparation
import me.bookk.feature.booking.domain.api.entity.BookingPreparation.PreparedService
import me.bookk.feature.booking.domain.api.entity.BookingService
import me.bookk.feature.booking.domain.api.entity.BookingSlot
import me.bookk.feature.booking.domain.api.entity.BookingSpecialist
import me.bookk.feature.booking.domain.api.entity.BookingTimetable
import me.bookk.feature.booking.presentation.FakeBookingStateFactory
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue
import kotlin.time.Clock
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Instant
import kotlin.uuid.Uuid

class BookingDateTimeViewModelTest {

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
    private val today = LocalDate(2026, 11, 24)
    private val tomorrow = LocalDate(2026, 11, 25)
    private val nextWeekDay = LocalDate(2026, 12, 3)
    private val haircut = BookingService(
        id = Uuid.random(),
        name = "Haircut",
        duration = 45.minutes,
        price = Money(90_000, Money.SupportedCurrency.UAH),
    )
    private val andrii = BookingSpecialist(Uuid.random(), "Andrii", "Koval")

    private inner class FakeGetBookingSlots : GetBookingSlots {
        val requests = mutableListOf<Pair<LocalDate, LocalDate>>()
        var free: Map<LocalDate, List<LocalTime>> = mapOf(
            today to listOf(LocalTime(9, 15), LocalTime(13, 0), LocalTime(18, 15)),
            tomorrow to listOf(LocalTime(10, 30)),
        )
        var closed: Set<LocalDate> = emptySet()
        var error: Throwable? = null

        override suspend fun invoke(
            businessId: Uuid,
            timetable: BookingTimetable,
            from: LocalDate,
            to: LocalDate,
        ): List<BookingDay> {
            requests += from to to
            error?.let { throw it }
            return generateSequence(from) { it.plus(1, DateTimeUnit.DAY) }
                .takeWhile { it <= to }
                .map { day(it) }
                .toList()
        }

        private fun day(date: LocalDate): BookingDay {
            return BookingDay(
                date = date,
                slots = free[date].orEmpty().map { slot(date, it) },
                isBusinessClosed = date in closed,
            )
        }
    }

    private val studio = BookingTimetable("Studio Lumen", kyiv, andrii)

    private inner class Fixture(
        savedTimetable: BookingTimetable? = studio,
    ) {
        val timetable = MutableStateFlow(savedTimetable)
        val preparation = MutableStateFlow(
            BookingPreparation(listOf(PreparedService(haircut, 1)), andrii.id),
        )
        val getBookingTimetable = mock<GetBookingTimetable> {
            every { flow(businessId) } returns timetable.filterNotNull()
            everySuspend { refresh(businessId) } returns Unit
        }
        val getBookingPreparation = mock<GetBookingPreparation> {
            every { flow(businessId) } returns preparation
        }
        val getBookingSlots = FakeGetBookingSlots()
        val setBookingSlot = mock<SetBookingSlot> {
            everySuspend { invoke(any(), any()) } returns Unit
        }
        val clock = object : Clock {
            override fun now(): Instant {
                return today.atTime(LocalTime(10, 0)).toInstant(kyiv)
            }
        }
        val errorMapper = FakeErrorMapper()

        fun sut(): BookingDateTimeViewModel {
            return BookingDateTimeViewModel(
                businessId = businessId,
                getBookingTimetable = getBookingTimetable,
                getBookingPreparation = getBookingPreparation,
                getBookingSlots = getBookingSlots,
                setBookingSlot = setBookingSlot,
                clock = clock,
                dateLocalizer = FakeDateLocalizer(),
                stateFactory = FakeBookingStateFactory(),
                vmArgs = VmArgs(errorMapper),
            )
        }
    }

    private fun slot(date: LocalDate, time: LocalTime): BookingSlot {
        return BookingSlot(date.atTime(time).toInstant(kyiv), time)
    }

    private fun weekOf(date: LocalDate): List<LocalDate> {
        val start = date.startOfWeek()
        return (0 until 7).map { start.plus(it, DateTimeUnit.DAY) }
    }

    private fun BookingDateTimeState.day(date: LocalDate): BookingDayItem {
        return days.first { it.id == date.toString() }
    }

    private fun BookingDateTimeState.slotItems(): List<SlotItem> {
        return slots.items.flatMap { it.slots }
    }

    private fun BookingDateTimeState.slotItem(time: LocalTime): SlotItem {
        return slotItems().first { it.time == time.toString().desc() }
    }

    @Test
    fun `shows the business and the specialist above the title`() = runUnitTest {
        given()
        val fixture = Fixture()

        whenn()
        val state = fixture.sut().uiState

        then()
        assertEquals("Studio Lumen".desc(), state.businessName)
        assertEquals("Andrii Koval".desc(), state.specialistName)
    }

    @Test
    fun `shows the week of today from the locale's first day`() = runUnitTest {
        given()
        val fixture = Fixture()

        whenn()
        val state = fixture.sut().uiState

        then()
        assertEquals(weekOf(today).map { it.toString() }, state.days.map { it.id })
        assertEquals(today.toString().desc(), state.monthTitle)
        assertTrue(state.day(today).isSelected)
    }

    @Test
    fun `asks for the free times of the shown week`() = runUnitTest {
        given()
        val fixture = Fixture()

        whenn()
        fixture.sut()

        then()
        val week = weekOf(today)
        assertEquals(listOf(week.first() to week.last()), fixture.getBookingSlots.requests)
    }

    @Test
    fun `marks today and disables the days before it`() = runUnitTest {
        given()
        val fixture = Fixture()

        whenn()
        val state = fixture.sut().uiState

        then()
        assertTrue(state.day(today).isToday)
        assertEquals(BookingRes.strings.booking_date_time_today.desc(), state.day(today).weekday)
        val past = weekOf(today).filter { it < today }.map { state.day(it) }
        assertTrue(past.none { it.isEnabled || it.isClosed })
    }

    @Test
    fun `strikes through and disables the days the business is closed on`() = runUnitTest {
        given()
        val fixture = Fixture()
        val closedDay = weekOf(today).last()
        fixture.getBookingSlots.closed = setOf(closedDay)

        whenn()
        val state = fixture.sut().uiState

        then()
        assertTrue(state.day(closedDay).isClosed)
        assertFalse(state.day(closedDay).isEnabled)
        assertTrue(state.day(tomorrow).isEnabled)
        assertFalse(state.day(tomorrow).isClosed)
    }

    @Test
    fun `keeps fully booked days tappable without striking them through`() = runUnitTest {
        given()
        val fixture = Fixture()
        val fullyBookedDay = weekOf(today).last()

        whenn()
        val state = fixture.sut().uiState

        then()
        assertTrue(state.day(fullyBookedDay).isEnabled)
        assertFalse(state.day(fullyBookedDay).isClosed)
    }

    @Test
    fun `groups the free times into morning, afternoon and evening`() = runUnitTest {
        given()
        val fixture = Fixture()

        whenn()
        val state = fixture.sut().uiState

        then()
        assertEquals(
            listOf(
                BookingRes.strings.booking_date_time_morning.desc(),
                BookingRes.strings.booking_date_time_afternoon.desc(),
                BookingRes.strings.booking_date_time_evening.desc(),
            ),
            state.slots.items.map { it.title },
        )
        assertEquals(
            listOf(listOf("09:15".desc()), listOf("13:00".desc()), listOf("18:15".desc())),
            state.slots.items.map { group -> group.slots.map { it.time } },
        )
    }

    @Test
    fun `opens on the first day with free times when today has none`() = runUnitTest {
        given()
        val fixture = Fixture()
        fixture.getBookingSlots.free = mapOf(tomorrow to listOf(LocalTime(10, 30)))

        whenn()
        val state = fixture.sut().uiState

        then()
        assertTrue(state.day(tomorrow).isSelected)
        assertEquals(listOf("10:30".desc()), state.slotItems().map { it.time })
    }

    @Test
    fun `says the business is closed on a closed day`() = runUnitTest {
        given()
        val fixture = Fixture()
        fixture.getBookingSlots.closed = setOf(tomorrow)
        fixture.getBookingSlots.free = mapOf(today to listOf(LocalTime(9, 15)))
        val state = fixture.sut().uiState

        whenn()
        state.datePicker.onDatePicked?.invoke(tomorrow)

        then()
        assertTrue(state.slots.items.isEmpty())
        assertEquals(
            BookingRes.strings.booking_date_time_closed.format("Studio Lumen"),
            state.slots.emptyState?.label,
        )
    }

    @Test
    fun `says there are no free times on an open day with none left`() = runUnitTest {
        given()
        val fixture = Fixture()
        fixture.getBookingSlots.free = mapOf(today to listOf(LocalTime(9, 15)))
        val state = fixture.sut().uiState

        whenn()
        state.day(tomorrow).onClick()

        then()
        assertTrue(state.slots.items.isEmpty())
        assertEquals(
            BookingRes.strings.booking_date_time_no_slots.desc(),
            state.slots.emptyState?.label,
        )
    }

    @Test
    fun `opens the date picker from today on`() = runUnitTest {
        given()
        val fixture = Fixture()
        val state = fixture.sut().uiState

        whenn()
        state.pickDateButton.onClick?.invoke()

        then()
        assertEquals(
            BookingRes.strings.booking_date_time_pick_date.desc(),
            state.pickDateButton.text,
        )
        assertTrue(state.datePicker.isDatePickerVisible)
        assertEquals(today, state.datePicker.minDate)
        assertEquals(today, state.datePicker.pickedDate)
    }

    @Test
    fun `shows the week of a picked date and asks for its free times`() = runUnitTest {
        given()
        val fixture = Fixture()
        fixture.getBookingSlots.free = mapOf(nextWeekDay to listOf(LocalTime(11, 0)))
        val state = fixture.sut().uiState

        whenn()
        state.datePicker.onDatePicked?.invoke(nextWeekDay)

        then()
        val week = weekOf(nextWeekDay)
        assertEquals(week.map { it.toString() }, state.days.map { it.id })
        assertEquals(week.first() to week.last(), fixture.getBookingSlots.requests.last())
        assertTrue(state.day(nextWeekDay).isSelected)
        assertEquals(nextWeekDay, state.datePicker.pickedDate)
        assertEquals(listOf("11:00".desc()), state.slotItems().map { it.time })
    }

    @Test
    fun `keeps a picked date that has no free times`() = runUnitTest {
        given()
        val fixture = Fixture()
        fixture.getBookingSlots.free = mapOf(today to listOf(LocalTime(9, 15)))
        val state = fixture.sut().uiState

        whenn()
        state.datePicker.onDatePicked?.invoke(nextWeekDay)

        then()
        assertTrue(state.day(nextWeekDay).isSelected)
        assertTrue(state.slots.items.isEmpty())
    }

    @Test
    fun `asks for the week's free times once while picking days within it`() = runUnitTest {
        given()
        val fixture = Fixture()
        val state = fixture.sut().uiState

        whenn()
        state.day(tomorrow).onClick()

        then()
        assertEquals(1, fixture.getBookingSlots.requests.size)
        assertTrue(state.day(tomorrow).isSelected)
        assertEquals(listOf("10:30".desc()), state.slotItems().map { it.time })
    }

    @Test
    fun `keeps Continue disabled until a time is picked`() = runUnitTest {
        given()
        val fixture = Fixture()

        whenn()
        val state = fixture.sut().uiState

        then()
        assertFalse(state.continueButton.isEnabled)
        assertEquals(
            BookingRes.strings.booking_date_time_pick_time.desc(),
            state.continueButton.text,
        )
        assertTrue(state.slotItems().none { it.isSelected })
    }

    @Test
    fun `selects a picked time and shows it with the price in the total bar`() = runUnitTest {
        given()
        val fixture = Fixture()
        val state = fixture.sut().uiState

        whenn()
        state.slotItem(LocalTime(13, 0)).onClick()

        then()
        assertEquals(
            listOf("13:00".desc()),
            state.slotItems().filter {
                it.isSelected
            }.map { it.time },
        )
        assertTrue(state.continueButton.isEnabled)
        assertEquals(DesignSystem.strings.action_continue.desc(), state.continueButton.text)
        assertEquals(
            BookingRes.strings.booking_date_time_summary.format(today.toString(), "13:00"),
            state.total?.summary,
        )
        assertEquals(
            Money(90_000, Money.SupportedCurrency.UAH).toString().desc(),
            state.total?.price,
        )
    }

    @Test
    fun `shows the services in the total bar until a time is picked`() = runUnitTest {
        given()
        val fixture = Fixture()

        whenn()
        val state = fixture.sut().uiState

        then()
        val total = assertNotNull(state.total)
        assertEquals(Money(90_000, Money.SupportedCurrency.UAH).toString().desc(), total.price)
        assertTrue(
            total.summary !=
                BookingRes.strings.booking_date_time_summary.format(today.toString(), "13:00"),
        )
    }

    @Test
    fun `forgets the picked time when another day is picked`() = runUnitTest {
        given()
        val fixture = Fixture()
        val state = fixture.sut().uiState
        state.slotItem(LocalTime(13, 0)).onClick()

        whenn()
        state.day(tomorrow).onClick()

        then()
        assertTrue(state.slotItems().none { it.isSelected })
        assertFalse(state.continueButton.isEnabled)
    }

    @Test
    fun `keeps the picked day and time when the booking data changes`() = runUnitTest {
        given()
        val fixture = Fixture()
        val state = fixture.sut().uiState
        state.day(tomorrow).onClick()
        state.slotItem(LocalTime(10, 30)).onClick()

        whenn()
        fixture.timetable.value = studio.copy(businessName = "Studio Lumen Two")

        then()
        assertEquals(1, fixture.getBookingSlots.requests.size)
        assertTrue(state.day(tomorrow).isSelected)
        assertEquals(
            listOf("10:30".desc()),
            state.slotItems().filter {
                it.isSelected
            }.map { it.time },
        )
        assertTrue(state.continueButton.isEnabled)
    }

    @Test
    fun `restores the approved day and time`() = runUnitTest {
        given()
        val fixture = Fixture()
        fixture.preparation.value = fixture.preparation.value.copy(
            slotStart = slot(tomorrow, LocalTime(10, 30)).start,
        )

        whenn()
        val state = fixture.sut().uiState

        then()
        assertTrue(state.day(tomorrow).isSelected)
        assertEquals(
            listOf("10:30".desc()),
            state.slotItems().filter {
                it.isSelected
            }.map { it.time },
        )
        assertTrue(state.continueButton.isEnabled)
    }

    @Test
    fun `leaves out an approved time that is no longer free`() = runUnitTest {
        given()
        val fixture = Fixture()
        fixture.preparation.value = fixture.preparation.value.copy(
            slotStart = slot(tomorrow, LocalTime(16, 0)).start,
        )

        whenn()
        val state = fixture.sut().uiState

        then()
        assertTrue(state.day(tomorrow).isSelected)
        assertTrue(state.slotItems().none { it.isSelected })
        assertFalse(state.continueButton.isEnabled)
    }

    @Test
    fun `saves the picked time and opens the review`() = runUnitTest {
        given()
        val fixture = Fixture()
        val state = fixture.sut().uiState
        state.slotItem(LocalTime(13, 0)).onClick()

        whenn()
        state.continueButton.onClick?.invoke()

        then()
        verifySuspend { fixture.setBookingSlot(businessId, slot(today, LocalTime(13, 0)).start) }
        assertEquals(
            listOf<BookingDateTimeDestinations>(BookingDateTimeDestinations.Review(businessId)),
            state.navigation.navigationDestination,
        )
    }

    @Test
    fun `stays on the step when saving the time fails`() = runUnitTest {
        given()
        val fixture = Fixture()
        everySuspend { fixture.setBookingSlot(any(), any()) } throws TestException()
        val state = fixture.sut().uiState
        state.slotItem(LocalTime(13, 0)).onClick()

        whenn()
        state.continueButton.onClick?.invoke()

        then()
        assertTrue(state.navigation.navigationDestination.isEmpty())
        fixture.errorMapper.assertMappedSingle(TestException::class)
        state.notifications.assertSingle<PresentationNotification.GlobalMessage>()
    }

    @Test
    fun `shows a failed load inline and loads again on retry`() = runUnitTest {
        given()
        val fixture = Fixture()
        fixture.getBookingSlots.error = TestException()
        val state = fixture.sut().uiState
        val error = assertNotNull(state.slots.errorState)
        fixture.getBookingSlots.error = null

        whenn()
        error.onRetryClick()

        then()
        assertEquals(2, fixture.getBookingSlots.requests.size)
        assertNull(state.slots.errorState)
        assertEquals(
            listOf("09:15", "13:00", "18:15").map {
                it.desc()
            },
            state.slotItems().map { it.time },
        )
        assertTrue(state.notifications.presentationNotification.isEmpty())
    }

    @Test
    fun `says the business takes no online bookings when booking is unavailable`() = runUnitTest {
        given()
        val fixture = Fixture()
        fixture.getBookingSlots.error = GetBookingSlots.Error.BookingUnavailable()

        whenn()
        val state = fixture.sut().uiState

        then()
        assertNull(state.slots.errorState)
        assertFalse(state.slots.isInitialLoading)
        assertEquals(
            BookingRes.strings.booking_date_time_unavailable.format("Studio Lumen"),
            state.slots.emptyState?.label,
        )
    }

    @Test
    fun `refreshes the business and its specialists when the screen opens`() = runUnitTest {
        given()
        val fixture = Fixture()

        whenn()
        fixture.sut()

        then()
        verifySuspend { fixture.getBookingTimetable.refresh(businessId) }
    }

    @Test
    fun `waits for the refreshed business when none is saved`() = runUnitTest {
        given()
        val fixture = Fixture(savedTimetable = null)
        val state = fixture.sut().uiState

        whenn()
        fixture.timetable.value = studio

        then()
        assertEquals(1, fixture.getBookingSlots.requests.size)
        assertEquals(
            listOf("09:15", "13:00", "18:15").map {
                it.desc()
            },
            state.slotItems().map { it.time },
        )
    }

    @Test
    fun `shows a failed refresh inline when no business is saved and retries it`() = runUnitTest {
        given()
        val fixture = Fixture(savedTimetable = null)
        everySuspend { fixture.getBookingTimetable.refresh(businessId) } throws TestException()
        val state = fixture.sut().uiState
        val error = assertNotNull(state.slots.errorState)
        everySuspend { fixture.getBookingTimetable.refresh(businessId) } returns Unit

        whenn()
        error.onRetryClick()
        fixture.timetable.value = studio

        then()
        assertFalse(state.slots.isInitialLoading)
        verifySuspend(VerifyMode.exactly(2)) { fixture.getBookingTimetable.refresh(businessId) }
        assertNull(state.slots.errorState)
        assertEquals(3, state.slotItems().size)
    }

    @Test
    fun `keeps the saved business on screen when its refresh fails`() = runUnitTest {
        given()
        val fixture = Fixture()
        everySuspend { fixture.getBookingTimetable.refresh(businessId) } throws TestException()

        whenn()
        val state = fixture.sut().uiState

        then()
        assertNull(state.slots.errorState)
        assertEquals(3, state.slotItems().size)
        assertTrue(state.notifications.presentationNotification.isEmpty())
    }

    @Test
    fun `leaves the booking when the business is not found`() = runUnitTest {
        given()
        val fixture = Fixture(savedTimetable = null)
        everySuspend { fixture.getBookingTimetable.refresh(businessId) } throws
            GetBookingTimetable.Error.BusinessNotFound()

        whenn()
        val state = fixture.sut().uiState

        then()
        assertEquals(
            listOf<BookingDateTimeDestinations>(BookingDateTimeDestinations.Leave),
            state.navigation.navigationDestination,
        )
    }

    @Test
    fun `leaves the booking when the picked services or specialist are missing`() = runUnitTest {
        given()
        val fixture = Fixture()
        fixture.getBookingSlots.error = GetBookingSlots.Error.SelectionInvalid()

        whenn()
        val state = fixture.sut().uiState

        then()
        assertEquals(
            listOf<BookingDateTimeDestinations>(BookingDateTimeDestinations.Leave),
            state.navigation.navigationDestination,
        )
        assertNull(state.slots.errorState)
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
            listOf<BookingDateTimeDestinations>(BookingDateTimeDestinations.Back),
            state.navigation.navigationDestination,
        )
        verifySuspend(VerifyMode.exactly(0)) { fixture.setBookingSlot(any(), any()) }
    }
}
