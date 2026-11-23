package me.bookk.feature.booking.presentation.screen.services

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
import me.bookk.designsystem.uistate.quantityOf
import me.bookk.feature.booking.domain.api.GetBookingCatalog
import me.bookk.feature.booking.domain.api.GetBookingPreparation
import me.bookk.feature.booking.domain.api.SetBookingServices
import me.bookk.feature.booking.domain.api.entity.BookingCatalog
import me.bookk.feature.booking.domain.api.entity.BookingPreparation
import me.bookk.feature.booking.domain.api.entity.BookingPreparation.PreparedService
import me.bookk.feature.booking.domain.api.entity.BookingService
import me.bookk.feature.booking.domain.api.entity.BookingServiceGroup
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

class BookingServicesViewModelTest {

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
    private val kidsHaircut = service("Kids haircut", 30.minutes, 40_000)
    private val beardTrim = service("Beard trim", 20.minutes, 30_000)
    private val hair = BookingServiceGroup(Uuid.random(), "Hair", listOf(haircut, kidsHaircut))
    private val beard = BookingServiceGroup(Uuid.random(), "Beard", listOf(beardTrim))
    private val catalog = BookingCatalog("Studio Lumen", listOf(hair, beard))
    private val nothingPrepared = BookingPreparation(emptyList())

    private inner class Fixture {
        val catalogs = MutableStateFlow(BookingCatalog(null, emptyList()))
        val preparation = MutableStateFlow(nothingPrepared)
        val getBookingCatalog = mock<GetBookingCatalog> {
            every { flow(businessId) } returns catalogs
            everySuspend { refresh(businessId) } returns catalog
        }
        val getBookingPreparation = mock<GetBookingPreparation> {
            every { flow(businessId) } returns preparation
        }
        val setBookingServices = mock<SetBookingServices> {
            everySuspend { invoke(any(), any()) } returns Unit
        }
        val errorMapper = FakeErrorMapper()

        fun sut() = BookingServicesViewModel(
            businessId = businessId,
            getBookingCatalog = getBookingCatalog,
            getBookingPreparation = getBookingPreparation,
            setBookingServices = setBookingServices,
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

    private fun prepared(vararg services: Pair<BookingService, Int>): BookingPreparation {
        return BookingPreparation(
            services.map { (service, quantity) ->
                PreparedService(service, quantity)
            },
        )
    }

    private fun BookingServicesState.option(service: BookingService): ServiceOption {
        return services.options.first { it.service.id == service.id }
    }

    private fun BookingServicesState.pick(service: BookingService, times: Int = 1) {
        repeat(times) { services.onQuantityIncreaseRequested(option(service)) }
    }

    @Test
    fun `shows the business name`() = runUnitTest {
        given()
        val fixture = Fixture()
        val state = fixture.sut().uiState

        whenn()
        fixture.catalogs.value = catalog

        then()
        assertEquals("Studio Lumen".desc(), state.businessName)
    }

    @Test
    fun `has no business name while the business is not cached`() = runUnitTest {
        given()
        val fixture = Fixture()

        whenn()
        val state = fixture.sut().uiState

        then()
        assertNull(state.businessName)
    }

    @Test
    fun `lists the services of each group in catalog order`() = runUnitTest {
        given()
        val fixture = Fixture()
        val state = fixture.sut().uiState

        whenn()
        fixture.catalogs.value = catalog

        then()
        assertEquals(listOf("Hair".desc(), "Beard".desc()), state.groups.items.map { it.name })
        assertEquals(
            listOf(listOf(haircut, kidsHaircut), listOf(beardTrim)),
            state.groups.items.map { group -> group.services.map { it.service } },
        )
        assertEquals(
            listOf(haircut, kidsHaircut, beardTrim),
            state.services.options.map { it.service },
        )
    }

    @Test
    fun `describes a service with its duration and price`() = runUnitTest {
        given()
        val fixture = Fixture()
        val state = fixture.sut().uiState

        whenn()
        fixture.catalogs.value = catalog

        then()
        assertEquals(
            BookingRes.strings.booking_services_details.format(
                DesignSystem.strings.common_duration_minutes.format(45),
                Money(60_000, Money.SupportedCurrency.UAH).toString(),
            ),
            state.option(haircut).details,
        )
    }

    @Test
    fun `lets the client pick several of the same service`() = runUnitTest {
        given()
        val fixture = Fixture()
        val state = fixture.sut().uiState
        fixture.catalogs.value = catalog

        whenn()
        state.pick(haircut, times = 2)

        then()
        assertTrue(state.services.isQuantityEditable)
        assertEquals(2, state.services.quantityOf(state.option(haircut)))
    }

    @Test
    fun `does not save the picks before the client continues`() = runUnitTest {
        given()
        val fixture = Fixture()
        val state = fixture.sut().uiState
        fixture.catalogs.value = catalog

        whenn()
        state.pick(haircut)
        state.services.onQuantityDecreaseRequested(state.option(haircut))

        then()
        verifySuspend(VerifyMode.not) { fixture.setBookingServices(any(), any()) }
    }

    @Test
    fun `shows the services saved for the business with their counts`() = runUnitTest {
        given()
        val fixture = Fixture()
        fixture.catalogs.value = catalog
        fixture.preparation.value = prepared(beardTrim to 2, haircut to 1)

        whenn()
        val state = fixture.sut().uiState

        then()
        assertEquals(2, state.services.quantityOf(state.option(beardTrim)))
        assertEquals(1, state.services.quantityOf(state.option(haircut)))
        assertEquals(0, state.services.quantityOf(state.option(kidsHaircut)))
    }

    @Test
    fun `keeps the client's unsaved picks when the saved ones change`() = runUnitTest {
        given()
        val fixture = Fixture()
        val state = fixture.sut().uiState
        fixture.catalogs.value = catalog
        state.pick(haircut)

        whenn()
        fixture.preparation.value = prepared(beardTrim to 1)

        then()
        assertEquals(1, state.services.quantityOf(state.option(haircut)))
        assertEquals(0, state.services.quantityOf(state.option(beardTrim)))
    }

    @Test
    fun `disables continue and asks to pick a service while nothing is picked`() = runUnitTest {
        given()
        val fixture = Fixture()

        whenn()
        val state = fixture.sut().uiState
        fixture.catalogs.value = catalog

        then()
        assertFalse(state.continueButton.isEnabled)
        assertEquals(BookingRes.strings.booking_services_pick.desc(), state.continueButton.text)
    }

    @Test
    fun `shows the total duration and price of the picked services on continue`() = runUnitTest {
        given()
        val fixture = Fixture()
        val state = fixture.sut().uiState
        fixture.catalogs.value = catalog

        whenn()
        state.pick(haircut)
        state.pick(beardTrim, times = 2)

        then()
        assertTrue(state.continueButton.isEnabled)
        assertEquals(
            BookingRes.strings.booking_services_continue.format(
                DesignSystem.strings.common_duration_hours_minutes.format(1, 25),
                Money(120_000, Money.SupportedCurrency.UAH).toString(),
            ),
            state.continueButton.text,
        )
    }

    @Test
    fun `shows the total of the saved services on continue`() = runUnitTest {
        given()
        val fixture = Fixture()
        fixture.catalogs.value = catalog
        fixture.preparation.value = prepared(haircut to 1, beardTrim to 2)

        whenn()
        val state = fixture.sut().uiState

        then()
        assertTrue(state.continueButton.isEnabled)
        assertEquals(
            BookingRes.strings.booking_services_continue.format(
                DesignSystem.strings.common_duration_hours_minutes.format(1, 25),
                Money(120_000, Money.SupportedCurrency.UAH).toString(),
            ),
            state.continueButton.text,
        )
    }

    @Test
    fun `disables continue again when the last picked service is removed`() = runUnitTest {
        given()
        val fixture = Fixture()
        val state = fixture.sut().uiState
        fixture.catalogs.value = catalog
        state.pick(haircut)

        whenn()
        state.services.onQuantityDecreaseRequested(state.option(haircut))

        then()
        assertFalse(state.continueButton.isEnabled)
        assertEquals(BookingRes.strings.booking_services_pick.desc(), state.continueButton.text)
    }

    @Test
    fun `saves the picked services with their counts when the client continues`() = runUnitTest {
        given()
        val fixture = Fixture()
        val state = fixture.sut().uiState
        fixture.catalogs.value = catalog
        state.pick(haircut)
        state.pick(beardTrim, times = 2)

        whenn()
        state.continueButton.onClick?.invoke()

        then()
        verifySuspend {
            fixture.setBookingServices(
                businessId,
                listOf(PreparedService(haircut, 1), PreparedService(beardTrim, 2)),
            )
        }
    }

    @Test
    fun `opens the specialist step once the picks are saved`() = runUnitTest {
        given()
        val fixture = Fixture()
        val state = fixture.sut().uiState
        fixture.catalogs.value = catalog
        state.pick(haircut)

        whenn()
        state.continueButton.onClick?.invoke()

        then()
        assertEquals(
            listOf<BookingServicesDestinations>(BookingServicesDestinations.Specialist(businessId)),
            state.navigation.navigationDestination,
        )
    }

    @Test
    fun `stays on the services and shows an error when the picks cannot be saved`() = runUnitTest {
        given()
        val fixture = Fixture()
        val state = fixture.sut().uiState
        fixture.catalogs.value = catalog
        state.pick(haircut)
        everySuspend { fixture.setBookingServices(any(), any()) } throws TestException()

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
            listOf<BookingServicesDestinations>(BookingServicesDestinations.Back),
            state.navigation.navigationDestination,
        )
    }

    @Test
    fun `keeps the picked count and uses the new price when the services change`() = runUnitTest {
        given()
        val fixture = Fixture()
        val state = fixture.sut().uiState
        fixture.catalogs.value = catalog
        state.pick(haircut, times = 2)
        val repriced = haircut.copy(price = Money(70_000, Money.SupportedCurrency.UAH))

        whenn()
        fixture.catalogs.value = BookingCatalog(
            "Studio Lumen",
            listOf(hair.copy(services = listOf(repriced))),
        )

        then()
        assertEquals(2, state.services.quantityOf(state.option(repriced)))
        assertEquals(
            BookingRes.strings.booking_services_continue.format(
                DesignSystem.strings.common_duration_hours_minutes.format(1, 30),
                Money(140_000, Money.SupportedCurrency.UAH).toString(),
            ),
            state.continueButton.text,
        )
    }

    @Test
    fun `drops a picked service the business stops offering`() = runUnitTest {
        given()
        val fixture = Fixture()
        val state = fixture.sut().uiState
        fixture.catalogs.value = catalog
        state.pick(beardTrim)

        whenn()
        fixture.catalogs.value = BookingCatalog("Studio Lumen", listOf(hair))

        then()
        assertTrue(state.services.selectedItems.isEmpty())
        assertFalse(state.continueButton.isEnabled)
    }

    @Test
    fun `keeps loading while the cache has no services yet`() = runUnitTest {
        given()
        val fixture = Fixture()
        everySuspend { fixture.getBookingCatalog.refresh(businessId) } throws TestException()

        whenn()
        val state = fixture.sut().uiState
        fixture.catalogs.value = BookingCatalog("Studio Lumen", emptyList())

        then()
        assertTrue(state.groups.isInitialLoading)
        assertEquals("Studio Lumen".desc(), state.businessName)
    }

    @Test
    fun `shows the empty state once the business turns out to have no services`() = runUnitTest {
        given()
        val fixture = Fixture()
        everySuspend {
            fixture.getBookingCatalog.refresh(any())
        } returns BookingCatalog("Studio Lumen", emptyList())

        whenn()
        val state = fixture.sut().uiState

        then()
        assertFalse(state.groups.isInitialLoading)
        assertTrue(state.groups.items.isEmpty())
        assertEquals(
            BookingRes.strings.booking_services_empty.desc(),
            state.groups.emptyState?.label,
        )
    }

    @Test
    fun `shows a load error inline when there is nothing cached`() = runUnitTest {
        given()
        val fixture = Fixture()
        everySuspend { fixture.getBookingCatalog.refresh(businessId) } throws TestException()

        whenn()
        val state = fixture.sut().uiState

        then()
        assertNotNull(state.groups.errorState)
        state.notifications.assertEmpty()
    }

    @Test
    fun `keeps the cached services and shows a retry banner when the refresh fails`() =
        runUnitTest {
            given()
            val fixture = Fixture()
            fixture.catalogs.value = catalog
            everySuspend { fixture.getBookingCatalog.refresh(businessId) } throws TestException()

            whenn()
            val state = fixture.sut().uiState

            then()
            assertEquals(2, state.groups.items.size)
            assertNull(state.groups.errorState)
            assertNotNull(state.groups.bannerError)
        }
}
