package me.bookk.feature.booking.presentation.screen.specialist

import dev.icerock.moko.resources.desc.Composition
import dev.icerock.moko.resources.desc.StringDesc
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
import library.money.api.Money
import me.bookk.android.designsystem.resources.DesignSystem
import me.bookk.android.feature.booking.resources.BookingRes
import me.bookk.core.presentation.VmArgs
import me.bookk.core.presentation.error.PresentationNotification
import me.bookk.core.test.given
import me.bookk.core.test.runUnitTest
import me.bookk.core.test.then
import me.bookk.core.test.whenn
import me.bookk.designsystem.test.FakeErrorMapper
import me.bookk.designsystem.test.TestException
import me.bookk.designsystem.test.ViewModelTestDispatchers
import me.bookk.designsystem.test.assertEmpty
import me.bookk.designsystem.test.assertMappedSingle
import me.bookk.designsystem.test.assertSingle
import me.bookk.feature.booking.domain.api.GetBookingPreparation
import me.bookk.feature.booking.domain.api.GetBookingSpecialists
import me.bookk.feature.booking.domain.api.SetBookingSpecialist
import me.bookk.feature.booking.domain.api.entity.BookingPreparation
import me.bookk.feature.booking.domain.api.entity.BookingPreparation.PreparedService
import me.bookk.feature.booking.domain.api.entity.BookingService
import me.bookk.feature.booking.domain.api.entity.BookingSpecialist
import me.bookk.feature.booking.presentation.FakeBookingStateFactory
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue
import kotlin.time.Duration
import kotlin.time.Duration.Companion.minutes
import kotlin.uuid.Uuid

class BookingSpecialistViewModelTest {

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
    private val haircut = service("Haircut", 45.minutes, 60_000)
    private val beardTrim = service("Beard trim", 20.minutes, 30_000)
    private val andrii = BookingSpecialist(Uuid.random(), "Andrii", "Koval")
    private val maria = BookingSpecialist(Uuid.random(), "Maria", "Shevchuk")

    private inner class Fixture {
        val preparation = MutableStateFlow(
            BookingPreparation(listOf(PreparedService(haircut, 1))),
        )
        val specialists = MutableStateFlow(emptyList<BookingSpecialist>())
        val getBookingPreparation = mock<GetBookingPreparation> {
            every { flow(businessId) } returns preparation
        }
        val getBookingSpecialists = mock<GetBookingSpecialists> {
            every { flow(businessId) } returns specialists
            everySuspend { refresh(businessId) } returns listOf(andrii, maria)
        }
        val setBookingSpecialist = mock<SetBookingSpecialist> {
            everySuspend { invoke(any(), any()) } returns Unit
        }
        val errorMapper = FakeErrorMapper()

        fun sut() = BookingSpecialistViewModel(
            businessId = businessId,
            getBookingPreparation = getBookingPreparation,
            getBookingSpecialists = getBookingSpecialists,
            setBookingSpecialist = setBookingSpecialist,
            stateFactory = FakeBookingStateFactory(),
            vmArgs = VmArgs(errorMapper),
        )
    }

    private fun service(name: String, duration: Duration, priceCents: Long) = BookingService(
        id = Uuid.random(),
        name = name,
        duration = duration,
        price = Money(priceCents, Money.SupportedCurrency.UAH),
    )

    private fun BookingSpecialistState.item(specialist: BookingSpecialist): SpecialistItem {
        return specialists.items.first { it.id == specialist.id.toString() }
    }

    @Test
    fun `sums up the picked services with their total duration and price`() = runUnitTest {
        given()
        val fixture = Fixture()
        fixture.preparation.value = BookingPreparation(
            listOf(PreparedService(haircut, 1), PreparedService(beardTrim, 2)),
        )

        whenn()
        val state = fixture.sut().uiState

        then()
        assertEquals(
            BookingRes.strings.booking_specialist_summary.format(
                StringDesc.Composition(
                    listOf(
                        "Haircut".desc(),
                        BookingRes.strings.booking_specialist_service_count.format("Beard trim", 2),
                    ),
                    ", ",
                ),
                DesignSystem.strings.common_duration_hours_minutes.format(1, 25),
                Money(120_000, Money.SupportedCurrency.UAH).toString(),
            ),
            state.summary,
        )
    }

    @Test
    fun `has no summary while nothing is picked`() = runUnitTest {
        given()
        val fixture = Fixture()
        fixture.preparation.value = BookingPreparation(emptyList())

        whenn()
        val state = fixture.sut().uiState

        then()
        assertNull(state.summary)
    }

    @Test
    fun `lists the specialists by full name with their initials`() = runUnitTest {
        given()
        val fixture = Fixture()
        val state = fixture.sut().uiState

        whenn()
        fixture.specialists.value = listOf(andrii, maria)

        then()
        assertEquals(
            listOf("Andrii Koval".desc(), "Maria Shevchuk".desc()),
            state.specialists.items.map { it.name },
        )
        assertEquals(listOf("AK", "MS"), state.specialists.items.map { it.initials })
    }

    @Test
    fun `uses the first name's initial alone when there is no last name`() = runUnitTest {
        given()
        val fixture = Fixture()
        val state = fixture.sut().uiState

        whenn()
        fixture.specialists.value = listOf(BookingSpecialist(Uuid.random(), "olena", ""))

        then()
        assertEquals("olena".desc(), state.specialists.items.single().name)
        assertEquals("O", state.specialists.items.single().initials)
    }

    @Test
    fun `keeps loading while no specialist is loaded yet`() = runUnitTest {
        given()
        val fixture = Fixture()
        everySuspend { fixture.getBookingSpecialists.refresh(businessId) } throws TestException()

        whenn()
        val state = fixture.sut().uiState

        then()
        assertTrue(state.specialists.isInitialLoading)
        assertTrue(state.specialists.items.isEmpty())
    }

    @Test
    fun `shows the empty state once nobody turns out to do every picked service`() = runUnitTest {
        given()
        val fixture = Fixture()
        everySuspend { fixture.getBookingSpecialists.refresh(businessId) } returns emptyList()

        whenn()
        val state = fixture.sut().uiState

        then()
        assertFalse(state.specialists.isInitialLoading)
        assertTrue(state.specialists.items.isEmpty())
        assertEquals(
            BookingRes.strings.booking_specialist_empty.desc(),
            state.specialists.emptyState?.label,
        )
    }

    @Test
    fun `shows a load error inline when the specialists cannot be loaded`() = runUnitTest {
        given()
        val fixture = Fixture()
        everySuspend { fixture.getBookingSpecialists.refresh(businessId) } throws TestException()

        whenn()
        val state = fixture.sut().uiState

        then()
        assertNotNull(state.specialists.errorState)
        state.notifications.assertEmpty()
    }

    @Test
    fun `saves the tapped specialist`() = runUnitTest {
        given()
        val fixture = Fixture()
        val state = fixture.sut().uiState
        fixture.specialists.value = listOf(andrii, maria)

        whenn()
        state.item(maria).onClick()

        then()
        verifySuspend { fixture.setBookingSpecialist(businessId, maria.id) }
    }

    @Test
    fun `opens the date and time step once the specialist is saved`() = runUnitTest {
        given()
        val fixture = Fixture()
        val state = fixture.sut().uiState
        fixture.specialists.value = listOf(andrii)

        whenn()
        state.item(andrii).onClick()

        then()
        assertEquals(
            listOf<BookingSpecialistDestinations>(
                BookingSpecialistDestinations.DateTime(businessId),
            ),
            state.navigation.navigationDestination,
        )
    }

    @Test
    fun `stays and shows an error when the specialist cannot be saved`() = runUnitTest {
        given()
        val fixture = Fixture()
        val state = fixture.sut().uiState
        fixture.specialists.value = listOf(andrii)
        everySuspend { fixture.setBookingSpecialist(any(), any()) } throws TestException()

        whenn()
        state.item(andrii).onClick()

        then()
        assertTrue(state.navigation.navigationDestination.isEmpty())
        fixture.errorMapper.assertMappedSingle(TestException::class)
        state.notifications.assertSingle<PresentationNotification.GlobalMessage>()
    }

    @Test
    fun `goes back when the back button is pressed`() = runUnitTest {
        given()
        val fixture = Fixture()
        val state = fixture.sut().uiState

        whenn()
        state.appBar.onBackClick?.invoke()

        then()
        assertEquals(
            listOf<BookingSpecialistDestinations>(BookingSpecialistDestinations.Back),
            state.navigation.navigationDestination,
        )
    }
}
