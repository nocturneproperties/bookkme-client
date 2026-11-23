package me.bookk.feature.booking.presentation.screen.specialist

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
import me.bookk.feature.booking.domain.api.GetBookingCatalog
import me.bookk.feature.booking.domain.api.GetBookingPreparation
import me.bookk.feature.booking.domain.api.GetBookingSpecialists
import me.bookk.feature.booking.domain.api.SetBookingSpecialist
import me.bookk.feature.booking.domain.api.entity.BookingCatalog
import me.bookk.feature.booking.domain.api.entity.BookingPreparation
import me.bookk.feature.booking.domain.api.entity.BookingPreparation.PreparedService
import me.bookk.feature.booking.domain.api.entity.BookingService
import me.bookk.feature.booking.domain.api.entity.BookingSpecialist
import me.bookk.feature.booking.presentation.FakeBookingStateFactory
import me.bookk.feature.booking.presentation.screen.BookingTotal
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
        val catalog = MutableStateFlow(BookingCatalog("Studio Lumen", emptyList()))
        val specialists = MutableStateFlow(emptyList<BookingSpecialist>())
        val getBookingCatalog = mock<GetBookingCatalog> {
            every { flow(businessId) } returns catalog
        }
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
            getBookingCatalog = getBookingCatalog,
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

    private fun BookingSpecialistState.selectedIds(): List<String> {
        return specialists.items.filter { it.isSelected }.map { it.id }
    }

    @Test
    fun `shows the business name above the title`() = runUnitTest {
        given()
        val fixture = Fixture()

        whenn()
        val state = fixture.sut().uiState

        then()
        assertEquals("Studio Lumen".desc(), state.businessName)
    }

    @Test
    fun `totals the approved services by count, duration and price`() = runUnitTest {
        given()
        val fixture = Fixture()
        fixture.preparation.value = BookingPreparation(
            listOf(PreparedService(haircut, 1), PreparedService(beardTrim, 2)),
        )

        whenn()
        val state = fixture.sut().uiState

        then()
        assertEquals(
            BookingTotal(
                summary = BookingRes.strings.booking_services_summary.format(
                    BookingRes.plurals.booking_services_count.format(3, 3),
                    DesignSystem.strings.common_duration_hours_minutes.format(1, 25),
                ),
                price = Money(120_000, Money.SupportedCurrency.UAH).toString().desc(),
            ),
            state.total,
        )
    }

    @Test
    fun `has no total while nothing is approved`() = runUnitTest {
        given()
        val fixture = Fixture()
        fixture.preparation.value = BookingPreparation(emptyList())

        whenn()
        val state = fixture.sut().uiState

        then()
        assertNull(state.total)
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
    fun `uses the first name alone and one initial when there is no last name`() = runUnitTest {
        given()
        val fixture = Fixture()
        val state = fixture.sut().uiState

        whenn()
        fixture.specialists.value = listOf(BookingSpecialist(Uuid.random(), "olena", " "))

        then()
        assertEquals("olena".desc(), state.specialists.items.single().name)
        assertEquals("O", state.specialists.items.single().initials)
    }

    @Test
    fun `preselects the first specialist`() = runUnitTest {
        given()
        val fixture = Fixture()
        val state = fixture.sut().uiState

        whenn()
        fixture.specialists.value = listOf(andrii, maria)

        then()
        assertEquals(listOf(andrii.id.toString()), state.selectedIds())
    }

    @Test
    fun `preselects the approved specialist when they are still listed`() = runUnitTest {
        given()
        val fixture = Fixture()
        fixture.preparation.value = BookingPreparation(
            listOf(PreparedService(haircut, 1)),
            specialistId = maria.id,
        )
        val state = fixture.sut().uiState

        whenn()
        fixture.specialists.value = listOf(andrii, maria)

        then()
        assertEquals(listOf(maria.id.toString()), state.selectedIds())
    }

    @Test
    fun `preselects the approved specialist even when they load after the list`() = runUnitTest {
        given()
        val fixture = Fixture()
        val state = fixture.sut().uiState
        fixture.specialists.value = listOf(andrii, maria)

        whenn()
        fixture.preparation.value = BookingPreparation(
            listOf(PreparedService(haircut, 1)),
            specialistId = maria.id,
        )

        then()
        assertEquals(listOf(maria.id.toString()), state.selectedIds())
    }

    @Test
    fun `preselects the first specialist when the approved one is no longer listed`() =
        runUnitTest {
            given()
            val fixture = Fixture()
            fixture.preparation.value = BookingPreparation(
                listOf(PreparedService(haircut, 1)),
                specialistId = Uuid.random(),
            )
            val state = fixture.sut().uiState

            whenn()
            fixture.specialists.value = listOf(andrii, maria)

            then()
            assertEquals(listOf(andrii.id.toString()), state.selectedIds())
        }

    @Test
    fun `selects the tapped specialist`() = runUnitTest {
        given()
        val fixture = Fixture()
        val state = fixture.sut().uiState
        fixture.specialists.value = listOf(andrii, maria)

        whenn()
        state.item(maria).onClick()

        then()
        assertEquals(listOf(maria.id.toString()), state.selectedIds())
    }

    @Test
    fun `keeps the tapped specialist selected when the list refreshes`() = runUnitTest {
        given()
        val fixture = Fixture()
        val state = fixture.sut().uiState
        fixture.specialists.value = listOf(andrii, maria)
        state.item(maria).onClick()

        whenn()
        fixture.specialists.value = listOf(andrii, maria.copy(lastName = "Shevchenko"))

        then()
        assertEquals(listOf(maria.id.toString()), state.selectedIds())
    }

    @Test
    fun `does not save the tapped specialist before continue is pressed`() = runUnitTest {
        given()
        val fixture = Fixture()
        val state = fixture.sut().uiState
        fixture.specialists.value = listOf(andrii, maria)

        whenn()
        state.item(maria).onClick()

        then()
        verifySuspend(dev.mokkery.verify.VerifyMode.not) {
            fixture.setBookingSpecialist(any(), any())
        }
    }

    @Test
    fun `names the selected specialist on the continue button`() = runUnitTest {
        given()
        val fixture = Fixture()
        val state = fixture.sut().uiState
        fixture.specialists.value = listOf(andrii, maria)

        whenn()
        state.item(maria).onClick()

        then()
        assertEquals(
            BookingRes.strings.booking_specialist_continue.format("Maria"),
            state.continueButton.text,
        )
        assertTrue(state.continueButton.isEnabled)
    }

    @Test
    fun `disables continue while nobody is listed`() = runUnitTest {
        given()
        val fixture = Fixture()
        everySuspend { fixture.getBookingSpecialists.refresh(businessId) } returns emptyList()

        whenn()
        val state = fixture.sut().uiState

        then()
        assertFalse(state.continueButton.isEnabled)
        assertEquals(DesignSystem.strings.action_continue.desc(), state.continueButton.text)
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
    fun `saves the selected specialist when continue is pressed`() = runUnitTest {
        given()
        val fixture = Fixture()
        val state = fixture.sut().uiState
        fixture.specialists.value = listOf(andrii, maria)
        state.item(maria).onClick()

        whenn()
        state.continueButton.onClick?.invoke()

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
        state.continueButton.onClick?.invoke()

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
        state.continueButton.onClick?.invoke()

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
