package me.bookk.feature.booking.presentation.screen.services

import dev.icerock.moko.resources.desc.desc
import dev.icerock.moko.resources.format
import dev.mokkery.answering.returns
import dev.mokkery.answering.throws
import dev.mokkery.every
import dev.mokkery.everySuspend
import dev.mokkery.matcher.any
import dev.mokkery.mock
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.datetime.TimeZone
import library.money.api.Currency
import library.money.api.Money
import me.bookk.android.feature.booking.resources.BookingRes
import me.bookk.core.presentation.VmArgs
import me.bookk.core.test.given
import me.bookk.core.test.runUnitTest
import me.bookk.core.test.then
import me.bookk.core.test.whenn
import me.bookk.designsystem.test.FakeErrorMapper
import me.bookk.designsystem.test.TestException
import me.bookk.designsystem.test.ViewModelTestDispatchers
import me.bookk.designsystem.test.assertEmpty
import me.bookk.designsystem.uistate.quantityOf
import me.bookk.feature.appointments.domain.api.entity.RequestedService
import me.bookk.feature.booking.presentation.FakeBookingStateFactory
import me.bookk.feature.business.domain.api.business.GetBusinesses
import me.bookk.feature.business.domain.api.entity.Business
import me.bookk.feature.business.domain.api.entity.BusinessPermissions
import me.bookk.feature.business.domain.api.entity.ResourcePermission
import me.bookk.feature.business.domain.api.entity.WorkingSchedule
import me.bookk.feature.services.domain.api.group.entity.ServiceGroup
import me.bookk.feature.services.domain.api.service.GetBusinessServices
import me.bookk.feature.services.domain.api.service.entity.Service
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
import kotlin.time.Instant
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
    private val hair = group("Hair", createdAtMs = 1_000)
    private val beard = group("Beard", createdAtMs = 2_000)
    private val haircut = service(hair, "Haircut", 45.minutes, 60_000)
    private val beardTrim = service(beard, "Beard trim", 20.minutes, 30_000)
    private val catalog = listOf(haircut, beardTrim)

    private inner class Fixture {
        val businesses = MutableStateFlow<List<Business>>(emptyList())
        val services = MutableStateFlow<List<Service>>(emptyList())
        val getBusinesses = mock<GetBusinesses> {
            every { flow(setOf(businessId)) } returns businesses
            everySuspend { refresh(setOf(businessId)) } returns listOf(business("Studio Lumen"))
        }
        val getBusinessServices = mock<GetBusinessServices> {
            every { flow(businessId) } returns services
            everySuspend { refresh(businessId) } returns catalog
        }
        val errorMapper = FakeErrorMapper()

        fun sut() = BookingServicesViewModel(
            businessId = businessId,
            getBusinesses = getBusinesses,
            getBusinessServices = getBusinessServices,
            stateFactory = FakeBookingStateFactory(),
            vmArgs = VmArgs(errorMapper),
        )
    }

    private fun group(name: String, createdAtMs: Long) = ServiceGroup(
        id = Uuid.random(),
        businessId = businessId,
        name = name,
        createdAt = Instant.fromEpochMilliseconds(createdAtMs),
    )

    private fun service(
        group: ServiceGroup,
        name: String,
        duration: Duration,
        priceCents: Long,
        isAvailable: Boolean = true,
    ) = Service(
        id = Uuid.random(),
        businessId = businessId,
        group = group,
        name = name,
        duration = duration,
        price = Money(priceCents, Money.SupportedCurrency.UAH),
        isAvailable = isAvailable,
        createdAt = Instant.fromEpochMilliseconds(0),
    )

    private fun business(name: String) = Business(
        id = businessId,
        name = name,
        description = "",
        address = "",
        location = null,
        currency = Currency("UAH"),
        timeZone = TimeZone.UTC,
        socials = emptyMap(),
        schedule = WorkingSchedule(),
        permissions = BusinessPermissions(
            business = ResourcePermission(),
            employees = ResourcePermission(),
            clients = ResourcePermission(),
            services = ResourcePermission(),
            appointments = ResourcePermission(),
        ),
    )

    private fun BookingServicesState.option(service: Service): ServiceOption {
        return services.options.first { it.service.id == service.id }
    }

    @Test
    fun `shows the business name and its initials`() = runUnitTest {
        given()
        val fixture = Fixture()
        val state = fixture.sut().uiState

        whenn()
        fixture.businesses.value = listOf(business("Studio Lumen"))

        then()
        assertEquals("Studio Lumen".desc(), state.businessName)
        assertEquals("SL", state.businessInitials)
    }

    @Test
    fun `uses the first letter of a one word business name as its initials`() = runUnitTest {
        given()
        val fixture = Fixture()
        val state = fixture.sut().uiState

        whenn()
        fixture.businesses.value = listOf(business("barbershop"))

        then()
        assertEquals("B", state.businessInitials)
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
    fun `lists the services grouped by service group in group order`() = runUnitTest {
        given()
        val fixture = Fixture()
        val state = fixture.sut().uiState
        val kidsHaircut = service(hair, "Kids haircut", 30.minutes, 40_000)

        whenn()
        fixture.services.value = listOf(beardTrim, haircut, kidsHaircut)

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
    fun `leaves out services the business does not offer`() = runUnitTest {
        given()
        val fixture = Fixture()
        val state = fixture.sut().uiState
        val hidden = service(hair, "Hidden", 30.minutes, 10_000, isAvailable = false)
        val hiddenBeard = service(beard, "Hidden beard", 30.minutes, 10_000, isAvailable = false)

        whenn()
        fixture.services.value = listOf(haircut, hidden, hiddenBeard)

        then()
        assertEquals(listOf("Hair".desc()), state.groups.items.map { it.name })
        assertEquals(listOf(haircut), state.services.options.map { it.service })
    }

    @Test
    fun `describes a service with its duration and price`() = runUnitTest {
        given()
        val fixture = Fixture()
        val state = fixture.sut().uiState

        whenn()
        fixture.services.value = catalog

        then()
        assertEquals(
            BookingRes.strings.booking_services_details.format(
                BookingRes.strings.booking_duration_minutes.format(45),
                Money(60_000, Money.SupportedCurrency.UAH).toString(),
            ),
            state.option(haircut).details,
        )
    }

    @Test
    fun `shows whole hours without minutes`() = runUnitTest {
        given()
        val fixture = Fixture()
        val state = fixture.sut().uiState
        val coloring = service(hair, "Coloring", 120.minutes, 100_000)

        whenn()
        fixture.services.value = listOf(coloring)

        then()
        assertEquals(
            BookingRes.strings.booking_services_details.format(
                BookingRes.strings.booking_duration_hours.format(2),
                Money(100_000, Money.SupportedCurrency.UAH).toString(),
            ),
            state.option(coloring).details,
        )
    }

    @Test
    fun `lets the client pick several of the same service`() = runUnitTest {
        given()
        val fixture = Fixture()
        val state = fixture.sut().uiState
        fixture.services.value = catalog

        whenn()
        state.services.onQuantityIncreaseRequested(state.option(haircut))
        state.services.onQuantityIncreaseRequested(state.option(haircut))

        then()
        assertTrue(state.services.isQuantityEditable)
        assertEquals(2, state.services.quantityOf(state.option(haircut)))
    }

    @Test
    fun `disables continue and asks to pick a service while nothing is picked`() = runUnitTest {
        given()
        val fixture = Fixture()

        whenn()
        val state = fixture.sut().uiState
        fixture.services.value = catalog

        then()
        assertFalse(state.continueButton.isEnabled)
        assertEquals(BookingRes.strings.booking_services_pick.desc(), state.continueButton.text)
    }

    @Test
    fun `shows the total duration and price of the picked services on continue`() = runUnitTest {
        given()
        val fixture = Fixture()
        val state = fixture.sut().uiState
        fixture.services.value = catalog

        whenn()
        state.services.onQuantityIncreaseRequested(state.option(haircut))
        state.services.onQuantityIncreaseRequested(state.option(beardTrim))
        state.services.onQuantityIncreaseRequested(state.option(beardTrim))

        then()
        assertTrue(state.continueButton.isEnabled)
        assertEquals(
            BookingRes.strings.booking_services_continue.format(
                BookingRes.strings.booking_duration_hours_minutes.format(1, 25),
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
        fixture.services.value = catalog
        state.services.onQuantityIncreaseRequested(state.option(haircut))

        whenn()
        state.services.onQuantityDecreaseRequested(state.option(haircut))

        then()
        assertFalse(state.continueButton.isEnabled)
        assertEquals(BookingRes.strings.booking_services_pick.desc(), state.continueButton.text)
    }

    @Test
    fun `opens the specialist step with the picked services and their counts`() = runUnitTest {
        given()
        val fixture = Fixture()
        val state = fixture.sut().uiState
        fixture.services.value = catalog
        state.services.onQuantityIncreaseRequested(state.option(haircut))
        state.services.onQuantityIncreaseRequested(state.option(beardTrim))
        state.services.onQuantityIncreaseRequested(state.option(beardTrim))

        whenn()
        state.continueButton.onClick?.invoke()

        then()
        assertEquals(
            listOf(
                BookingServicesDestinations.Specialist(
                    businessId,
                    listOf(RequestedService(haircut.id, 1), RequestedService(beardTrim.id, 2)),
                ),
            ),
            state.navigation.navigationDestination,
        )
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
        fixture.services.value = catalog
        state.services.onQuantityIncreaseRequested(state.option(haircut))
        state.services.onQuantityIncreaseRequested(state.option(haircut))
        val repriced = haircut.copy(price = Money(70_000, Money.SupportedCurrency.UAH))

        whenn()
        fixture.services.value = listOf(repriced)

        then()
        assertEquals(2, state.services.quantityOf(state.option(repriced)))
        assertEquals(
            BookingRes.strings.booking_services_continue.format(
                BookingRes.strings.booking_duration_hours_minutes.format(1, 30),
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
        fixture.services.value = catalog
        state.services.onQuantityIncreaseRequested(state.option(beardTrim))

        whenn()
        fixture.services.value = listOf(haircut, beardTrim.copy(isAvailable = false))

        then()
        assertTrue(state.services.selectedItems.isEmpty())
        assertFalse(state.continueButton.isEnabled)
    }

    @Test
    fun `keeps loading while the cache has no services yet`() = runUnitTest {
        given()
        val fixture = Fixture()
        everySuspend { fixture.getBusinessServices.refresh(businessId) } throws TestException()

        whenn()
        val state = fixture.sut().uiState
        fixture.services.value = emptyList()

        then()
        assertTrue(state.groups.isInitialLoading)
    }

    @Test
    fun `shows the empty state once the business turns out to have no services`() = runUnitTest {
        given()
        val fixture = Fixture()
        everySuspend { fixture.getBusinessServices.refresh(any()) } returns emptyList()

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
        everySuspend { fixture.getBusinessServices.refresh(businessId) } throws TestException()

        whenn()
        val state = fixture.sut().uiState

        then()
        assertNotNull(state.groups.errorState)
        state.notifications.assertEmpty()
    }

    @Test
    fun `shows a load error when the business cannot be refreshed`() = runUnitTest {
        given()
        val fixture = Fixture()
        everySuspend { fixture.getBusinesses.refresh(setOf(businessId)) } throws TestException()

        whenn()
        val state = fixture.sut().uiState

        then()
        assertNotNull(state.groups.errorState)
    }

    @Test
    fun `keeps the cached services and shows a retry banner when the refresh fails`() =
        runUnitTest {
            given()
            val fixture = Fixture()
            fixture.services.value = catalog
            everySuspend { fixture.getBusinessServices.refresh(businessId) } throws TestException()

            whenn()
            val state = fixture.sut().uiState

            then()
            assertEquals(2, state.groups.items.size)
            assertNull(state.groups.errorState)
            assertNotNull(state.groups.bannerError)
        }
}
