package me.bookk.feature.dashboard.presentation.screen.clienthome

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
import kotlinx.coroutines.awaitCancellation
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atStartOfDayIn
import library.money.api.Money
import me.bookk.android.feature.dashboard.resources.DashboardRes
import me.bookk.core.domain.launch.ConfirmableAction
import me.bookk.core.presentation.VmArgs
import me.bookk.core.presentation.error.PresentationNotification
import me.bookk.core.test.given
import me.bookk.core.test.runUnitTest
import me.bookk.core.test.then
import me.bookk.core.test.whenn
import me.bookk.designsystem.resources.color.ColorToken
import me.bookk.designsystem.simple
import me.bookk.designsystem.test.FakeDateLocalizer
import me.bookk.designsystem.test.FakeErrorMapper
import me.bookk.designsystem.test.TestException
import me.bookk.designsystem.test.ViewModelTestDispatchers
import me.bookk.designsystem.test.assertEmpty
import me.bookk.designsystem.test.assertMappedSingle
import me.bookk.designsystem.test.assertSingle
import me.bookk.designsystem.test.failOnceThenSuspend
import me.bookk.feature.appointments.domain.api.GetSavedBusinesses
import me.bookk.feature.appointments.domain.api.GetUpcomingBookings
import me.bookk.feature.appointments.domain.api.SaveBusiness
import me.bookk.feature.appointments.domain.api.entity.EmployeeSnapshot
import me.bookk.feature.appointments.domain.api.entity.SavedBusiness
import me.bookk.feature.appointments.domain.api.entity.ServiceSnapshot
import me.bookk.feature.appointments.domain.api.entity.UpcomingBooking
import me.bookk.feature.authorization.domain.api.UserProfileCRUD
import me.bookk.feature.authorization.domain.entity.UserProfile
import me.bookk.feature.business.domain.api.entity.BusinessLink
import me.bookk.feature.business.domain.api.link.ResolveBusinessLink
import me.bookk.feature.dashboard.presentation.FakeClientDashboardStateFactory
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Instant
import kotlin.uuid.Uuid

class ClientHomeViewModelTest {

    private val dispatchers = ViewModelTestDispatchers()

    @BeforeTest
    fun setUp() {
        dispatchers.install()
    }

    @AfterTest
    fun tearDown() {
        dispatchers.uninstall()
    }

    private class Fixture {
        val profile = MutableStateFlow<UserProfile?>(null)
        val upcoming = MutableStateFlow<List<UpcomingBooking>>(emptyList())
        val businesses = MutableStateFlow<List<SavedBusiness>>(emptyList())
        val getUpcomingBookings = mock<GetUpcomingBookings> {
            every { flow() } returns upcoming
            everySuspend { refresh() } returns emptyList()
        }
        val getSavedBusinesses = mock<GetSavedBusinesses> {
            every { flow() } returns businesses
            everySuspend { refresh() } returns emptyList()
        }
        val saveBusiness = mock<SaveBusiness> {
            everySuspend { invoke(any()) } returns Unit
        }
        val userProfileCRUD = mock<UserProfileCRUD> {
            every { observe() } returns profile
        }
        val linkedBusinessId: Uuid = Uuid.random()
        val resolveBusinessLink = mock<ResolveBusinessLink> {
            everySuspend { invoke(any()) } returns BusinessLink(linkedBusinessId, "my-salon")
        }
        val errorMapper = FakeErrorMapper()

        fun sut(startParameter: String = "") = ClientHomeViewModel(
            startParameter = startParameter,
            getUpcomingBookings = getUpcomingBookings,
            getSavedBusinesses = getSavedBusinesses,
            saveBusiness = saveBusiness,
            resolveBusinessLink = resolveBusinessLink,
            userProfileCRUD = userProfileCRUD,
            stateFactory = FakeClientDashboardStateFactory(),
            dateLocalizer = FakeDateLocalizer(),
            vmArgs = VmArgs(errorMapper),
        )
    }

    private fun booking(
        status: UpcomingBooking.Status = UpcomingBooking.Status.CONFIRMED,
        services: List<ServiceSnapshot> = listOf(service("Haircut")),
    ) = UpcomingBooking(
        id = Uuid.random(),
        businessId = Uuid.random(),
        businessName = "Studio Lumen",
        services = services,
        employee = EmployeeSnapshot(Uuid.random(), Uuid.random(), "Andrii"),
        date = LocalDateTime(2026, 11, 24, 14, 30),
        status = status,
    )

    private fun service(name: String, id: Uuid = Uuid.random()) = ServiceSnapshot(
        id,
        name,
        Uuid.random(),
        Money(1000L, Money.SupportedCurrency.UAH),
        30.minutes,
    )

    private fun savedBusiness(
        name: String = "Studio Lumen",
        isEnabled: Boolean = true,
        lastVisitAt: Instant? = null,
    ) = SavedBusiness(
        id = Uuid.random(),
        name = name,
        isEnabled = isEnabled,
        savedAt = Instant.fromEpochMilliseconds(0),
        lastVisitAt = lastVisitAt,
    )

    private suspend fun TestScope.loadedSut(
        fixture: Fixture,
        startParameter: String = "",
    ): ClientHomeViewModel {
        val sut = fixture.sut(startParameter)
        advanceUntilIdle()
        return sut
    }

    @Test
    fun `greets the user by first name`() = runUnitTest {
        given()
        val fixture = Fixture()
        fixture.profile.value = UserProfile(Uuid.random(), "Olena", "Koval", "olena@example.com")

        whenn()
        val sut = loadedSut(fixture)

        then()
        assertEquals(
            DashboardRes.strings.dashboard_client_home_greeting.format("Olena"),
            sut.uiState.greeting,
        )
    }

    @Test
    fun `greets without a name while no profile is stored`() = runUnitTest {
        given()
        val fixture = Fixture()

        whenn()
        val sut = loadedSut(fixture)

        then()
        assertEquals(
            DashboardRes.strings.dashboard_client_home_greeting_default.desc(),
            sut.uiState.greeting,
        )
    }

    @Test
    fun `greets without a name while the profile has none`() = runUnitTest {
        given()
        val fixture = Fixture()
        fixture.profile.value = UserProfile(Uuid.random(), " ", "", "olena@example.com")

        whenn()
        val sut = loadedSut(fixture)

        then()
        assertEquals(
            DashboardRes.strings.dashboard_client_home_greeting_default.desc(),
            sut.uiState.greeting,
        )
    }

    @Test
    fun `refreshes upcoming bookings and saved businesses on start`() = runUnitTest {
        given()
        val fixture = Fixture()

        whenn()
        loadedSut(fixture)

        then()
        verifySuspend { fixture.getUpcomingBookings.refresh() }
        verifySuspend { fixture.getSavedBusinesses.refresh() }
    }

    @Test
    fun `renders an upcoming booking with day, time and services`() = runUnitTest {
        given()
        val fixture = Fixture()
        val upcoming = booking(services = listOf(service("Haircut"), service("Beard trim")))
        val sut = loadedSut(fixture)

        whenn()
        fixture.upcoming.value = listOf(upcoming)

        then()
        val item = sut.uiState.upcoming.items.single()
        assertEquals(upcoming.id.toString(), item.id)
        assertEquals("2026-11-24", item.weekday)
        assertEquals("24", item.day)
        assertEquals("Studio Lumen", item.businessName)
        assertEquals(
            DashboardRes.strings.dashboard_client_home_time_and_services.format(
                "14:30",
                "Haircut, Beard trim",
            ),
            item.summary,
        )
    }

    @Test
    fun `counts a service booked more than once`() = runUnitTest {
        given()
        val fixture = Fixture()
        val haircut = service("Haircut")
        val sut = loadedSut(fixture)

        whenn()
        fixture.upcoming.value =
            listOf(booking(services = listOf(haircut, service("Beard trim"), haircut)))

        then()
        assertEquals(
            DashboardRes.strings.dashboard_client_home_time_and_services.format(
                "14:30",
                "Haircut × 2, Beard trim",
            ),
            sut.uiState.upcoming.items.single().summary,
        )
    }

    @Test
    fun `shows a confirmed booking in the success colour`() = runUnitTest {
        given()
        val fixture = Fixture()
        val sut = loadedSut(fixture)

        whenn()
        fixture.upcoming.value = listOf(booking(UpcomingBooking.Status.CONFIRMED))

        then()
        val item = sut.uiState.upcoming.items.single()
        assertEquals(
            DashboardRes.strings.dashboard_client_home_status_confirmed.desc(),
            item.status,
        )
        assertEquals(ColorToken.Success, item.statusColor)
    }

    @Test
    fun `shows a waiting booking in the secondary colour`() = runUnitTest {
        given()
        val fixture = Fixture()
        val sut = loadedSut(fixture)

        whenn()
        fixture.upcoming.value = listOf(booking(UpcomingBooking.Status.WAITING_FOR_CONFIRMATION))

        then()
        val item = sut.uiState.upcoming.items.single()
        assertEquals(
            DashboardRes.strings.dashboard_client_home_status_waiting.desc(),
            item.status,
        )
        assertEquals(ColorToken.SecondaryText, item.statusColor)
    }

    @Test
    fun `opens the request details when a waiting booking is tapped`() = runUnitTest {
        given()
        val fixture = Fixture()
        val upcoming = booking(status = UpcomingBooking.Status.WAITING_FOR_CONFIRMATION)
        val sut = loadedSut(fixture)
        fixture.upcoming.value = listOf(upcoming)

        whenn()
        sut.uiState.upcoming.items.single().onClick()

        then()
        assertEquals(
            listOf<ClientHomeDestinations>(ClientHomeDestinations.RequestDetails(upcoming.id)),
            sut.uiState.navigation.navigationDestination,
        )
    }

    @Test
    fun `opens the appointment details when a confirmed booking is tapped`() = runUnitTest {
        given()
        val fixture = Fixture()
        val upcoming = booking(status = UpcomingBooking.Status.CONFIRMED)
        val sut = loadedSut(fixture)
        fixture.upcoming.value = listOf(upcoming)

        whenn()
        sut.uiState.upcoming.items.single().onClick()

        then()
        assertEquals(
            listOf<ClientHomeDestinations>(ClientHomeDestinations.AppointmentDetails(upcoming.id)),
            sut.uiState.navigation.navigationDestination,
        )
    }

    @Test
    fun `opens all bookings`() = runUnitTest {
        given()
        val fixture = Fixture()
        val sut = loadedSut(fixture)

        whenn()
        sut.uiState.allBookingsButton.onClick?.invoke()

        then()
        assertEquals(
            listOf<ClientHomeDestinations>(ClientHomeDestinations.Bookings),
            sut.uiState.navigation.navigationDestination,
        )
    }

    @Test
    fun `opens all businesses`() = runUnitTest {
        given()
        val fixture = Fixture()
        val sut = loadedSut(fixture)

        whenn()
        sut.uiState.allBusinessesButton.onClick?.invoke()

        then()
        assertEquals(
            listOf<ClientHomeDestinations>(ClientHomeDestinations.Businesses),
            sut.uiState.navigation.navigationDestination,
        )
    }

    @Test
    fun `opens settings`() = runUnitTest {
        given()
        val fixture = Fixture()
        val sut = loadedSut(fixture)

        whenn()
        sut.uiState.settingsButton.onClick?.invoke()

        then()
        assertEquals(
            listOf<ClientHomeDestinations>(ClientHomeDestinations.Settings),
            sut.uiState.navigation.navigationDestination,
        )
    }

    @Test
    fun `renders a saved business with its last visit`() = runUnitTest {
        given()
        val fixture = Fixture()
        val lastVisitAt = LocalDate(2026, 11, 12).atStartOfDayIn(TimeZone.currentSystemDefault())
        val business = savedBusiness(name = "studio lumen", lastVisitAt = lastVisitAt)
        val sut = loadedSut(fixture)

        whenn()
        fixture.businesses.value = listOf(business)

        then()
        val item = sut.uiState.businesses.items.single()
        assertEquals(business.id.toString(), item.id)
        assertEquals("studio lumen", item.name)
        assertEquals(
            DashboardRes.strings.dashboard_client_home_last_visit.format("2026-11-12"),
            item.subtitle,
        )
    }

    @Test
    fun `says a saved business was never visited`() = runUnitTest {
        given()
        val fixture = Fixture()
        val sut = loadedSut(fixture)

        whenn()
        fixture.businesses.value = listOf(savedBusiness(lastVisitAt = null))

        then()
        assertEquals(
            DashboardRes.strings.dashboard_client_home_no_visits.desc(),
            sut.uiState.businesses.items.single().subtitle,
        )
    }

    @Test
    fun `says a saved business does not take bookings`() = runUnitTest {
        given()
        val fixture = Fixture()
        val sut = loadedSut(fixture)

        whenn()
        fixture.businesses.value =
            listOf(savedBusiness(isEnabled = false, lastVisitAt = Instant.fromEpochMilliseconds(0)))

        then()
        assertEquals(
            DashboardRes.strings.dashboard_client_home_not_taking_bookings.desc(),
            sut.uiState.businesses.items.single().subtitle,
        )
    }

    @Test
    fun `opens the services of a saved business when tapped`() = runUnitTest {
        given()
        val fixture = Fixture()
        val business = savedBusiness()
        val sut = loadedSut(fixture)
        fixture.businesses.value = listOf(business)

        whenn()
        sut.uiState.businesses.items.single().onClick()

        then()
        assertEquals(
            listOf<ClientHomeDestinations>(ClientHomeDestinations.Services(business.id)),
            sut.uiState.navigation.navigationDestination,
        )
    }

    @Test
    fun `loads the screen until the profile is read`() = runUnitTest {
        given()
        val fixture = Fixture()
        every { fixture.userProfileCRUD.observe() } returns MutableSharedFlow()

        whenn()
        val sut = loadedSut(fixture)

        then()
        assertTrue(sut.uiState.isLoading)
        assertNull(sut.uiState.greeting)
    }

    @Test
    fun `loads the screen until upcoming bookings load`() = runUnitTest {
        given()
        val fixture = Fixture()
        everySuspend { fixture.getUpcomingBookings.refresh() } calls { awaitCancellation() }

        whenn()
        val sut = loadedSut(fixture)

        then()
        assertTrue(sut.uiState.isLoading)
    }

    @Test
    fun `loads the screen until saved businesses load`() = runUnitTest {
        given()
        val fixture = Fixture()
        everySuspend { fixture.getSavedBusinesses.refresh() } calls { awaitCancellation() }

        whenn()
        val sut = loadedSut(fixture)

        then()
        assertTrue(sut.uiState.isLoading)
    }

    @Test
    fun `shows the screen once the profile and both lists load`() = runUnitTest {
        given()
        val fixture = Fixture()

        whenn()
        val sut = loadedSut(fixture)

        then()
        assertFalse(sut.uiState.isLoading)
    }

    @Test
    fun `shows the screen with cached businesses before their refresh ends`() = runUnitTest {
        given()
        val fixture = Fixture()
        fixture.businesses.value = listOf(savedBusiness())
        everySuspend { fixture.getSavedBusinesses.refresh() } calls { awaitCancellation() }

        whenn()
        val sut = loadedSut(fixture)

        then()
        assertFalse(sut.uiState.isLoading)
    }

    @Test
    fun `shows the screen when a list fails to load`() = runUnitTest {
        given()
        val fixture = Fixture()
        everySuspend { fixture.getUpcomingBookings.refresh() } throws TestException()

        whenn()
        val sut = loadedSut(fixture)

        then()
        assertFalse(sut.uiState.isLoading)
    }

    @Test
    fun `shows no business yet once both lists load empty`() = runUnitTest {
        given()
        val fixture = Fixture()

        whenn()
        val sut = loadedSut(fixture)

        then()
        assertTrue(sut.uiState.hasNoBusiness)
    }

    @Test
    fun `does not show no business yet while lists are loading`() = runUnitTest {
        given()
        val fixture = Fixture()
        everySuspend { fixture.getUpcomingBookings.refresh() } throws TestException()

        whenn()
        val sut = loadedSut(fixture)

        then()
        assertFalse(sut.uiState.hasNoBusiness)
    }

    @Test
    fun `does not show no business yet when a saved business exists`() = runUnitTest {
        given()
        val fixture = Fixture()
        val sut = loadedSut(fixture)

        whenn()
        fixture.businesses.value = listOf(savedBusiness())

        then()
        assertFalse(sut.uiState.hasNoBusiness)
        assertTrue(sut.uiState.hasNoUpcomingBookings)
    }

    @Test
    fun `describes an empty upcoming list`() = runUnitTest {
        given()
        val fixture = Fixture()

        whenn()
        val sut = loadedSut(fixture)

        then()
        assertEquals(
            DashboardRes.strings.dashboard_client_home_no_upcoming.desc(),
            sut.uiState.upcoming.emptyState?.label,
        )
    }

    @Test
    fun `hides book again when no business is saved`() = runUnitTest {
        given()
        val fixture = Fixture()
        val sut = loadedSut(fixture)

        whenn()
        fixture.upcoming.value = listOf(booking())

        then()
        assertFalse(sut.uiState.showsBookAgain)
    }

    @Test
    fun `shows book again while saved businesses load`() = runUnitTest {
        given()
        val fixture = Fixture()
        everySuspend { fixture.getSavedBusinesses.refresh() } throws TestException()

        whenn()
        val sut = loadedSut(fixture)

        then()
        assertTrue(sut.uiState.showsBookAgain)
    }

    @Test
    fun `keeps cached bookings and shows a banner when refresh fails`() = runUnitTest {
        given()
        val fixture = Fixture()
        fixture.upcoming.value = listOf(booking())
        everySuspend { fixture.getUpcomingBookings.refresh() } throws TestException()

        whenn()
        val sut = loadedSut(fixture)

        then()
        assertEquals(1, sut.uiState.upcoming.items.size)
        assertNotNull(sut.uiState.upcoming.bannerError)
    }

    @Test
    fun `shows the upcoming flow failure as a notification`() = runUnitTest {
        given()
        val fixture = Fixture()
        every { fixture.getUpcomingBookings.flow() } returns failOnceThenSuspend()

        whenn()
        val sut = loadedSut(fixture)

        then()
        fixture.errorMapper.assertMappedSingle(TestException::class)
        sut.uiState.notifications.assertSingle<PresentationNotification>()
    }

    @Test
    fun `opens the business from the launch parameter`() = runUnitTest {
        given()
        val fixture = Fixture()
        val businessId = Uuid.random()

        whenn()
        val sut = loadedSut(fixture, startParameter = businessId.toString())

        then()
        assertEquals(
            listOf<ClientHomeDestinations>(ClientHomeDestinations.Services(businessId)),
            sut.uiState.navigation.navigationDestination,
        )
    }

    @Test
    fun `saves the business from the launch parameter`() = runUnitTest {
        given()
        val fixture = Fixture()
        val businessId = Uuid.random()

        whenn()
        loadedSut(fixture, startParameter = businessId.toString())

        then()
        verifySuspend { fixture.saveBusiness(businessId) }
    }

    @Test
    fun `lets the save refresh saved businesses after a launch with a business`() = runUnitTest {
        given()
        val fixture = Fixture()

        whenn()
        loadedSut(fixture, startParameter = Uuid.random().toString())

        then()
        verifySuspend(VerifyMode.exactly(0)) { fixture.getSavedBusinesses.refresh() }
    }

    @Test
    fun `does not look up a link for a launch parameter that is a business id`() = runUnitTest {
        given()
        val fixture = Fixture()

        whenn()
        loadedSut(fixture, startParameter = Uuid.random().toString())

        then()
        verifySuspend(VerifyMode.exactly(0)) { fixture.resolveBusinessLink(any()) }
    }

    @Test
    fun `does not look up a link without a launch parameter`() = runUnitTest {
        given()
        val fixture = Fixture()

        whenn()
        loadedSut(fixture)

        then()
        verifySuspend(VerifyMode.exactly(0)) { fixture.resolveBusinessLink(any()) }
    }

    @Test
    fun `looks up the launch parameter as a link name`() = runUnitTest {
        given()
        val fixture = Fixture()

        whenn()
        loadedSut(fixture, startParameter = "my-salon")

        then()
        verifySuspend { fixture.resolveBusinessLink("my-salon") }
    }

    @Test
    fun `looks up an id without dashes as a link name`() = runUnitTest {
        given()
        val fixture = Fixture()
        val name = Uuid.random().toHexString()

        whenn()
        loadedSut(fixture, startParameter = name)

        then()
        verifySuspend { fixture.resolveBusinessLink(name) }
    }

    @Test
    fun `opens the business behind the launch link`() = runUnitTest {
        given()
        val fixture = Fixture()

        whenn()
        val sut = loadedSut(fixture, startParameter = "my-salon")

        then()
        assertEquals(
            listOf<ClientHomeDestinations>(
                ClientHomeDestinations.Services(fixture.linkedBusinessId),
            ),
            sut.uiState.navigation.navigationDestination,
        )
    }

    @Test
    fun `saves the business behind the launch link`() = runUnitTest {
        given()
        val fixture = Fixture()

        whenn()
        loadedSut(fixture, startParameter = "my-salon")

        then()
        verifySuspend { fixture.saveBusiness(fixture.linkedBusinessId) }
        verifySuspend(VerifyMode.exactly(0)) { fixture.getSavedBusinesses.refresh() }
    }

    @Test
    fun `explains a launch link that leads to no business`() = runUnitTest {
        given()
        val fixture = Fixture()
        everySuspend { fixture.resolveBusinessLink(any()) } throws
            ResolveBusinessLink.Error.NotFound()

        whenn()
        val sut = loadedSut(fixture, startParameter = "promo")

        then()
        val message = sut.uiState.notifications.assertSingle<PresentationNotification.Message>()
        assertEquals(
            DashboardRes.strings.dashboard_client_home_link_not_found.desc(),
            message.message,
        )
        assertTrue(sut.uiState.navigation.navigationDestination.isEmpty())
        verifySuspend(VerifyMode.exactly(0)) { fixture.saveBusiness(any()) }
    }

    @Test
    fun `refreshes saved businesses itself when the launch link leads nowhere`() = runUnitTest {
        given()
        val fixture = Fixture()
        everySuspend { fixture.resolveBusinessLink(any()) } throws
            ResolveBusinessLink.Error.NotFound()

        whenn()
        loadedSut(fixture, startParameter = "promo")

        then()
        verifySuspend { fixture.getSavedBusinesses.refresh() }
    }

    @Test
    fun `shows a failed launch link lookup and refreshes saved businesses`() = runUnitTest {
        given()
        val fixture = Fixture()
        everySuspend { fixture.resolveBusinessLink(any()) } throws TestException()

        whenn()
        val sut = loadedSut(fixture, startParameter = "my-salon")

        then()
        fixture.errorMapper.assertMappedSingle(TestException::class)
        sut.uiState.notifications.assertSingle<PresentationNotification>()
        verifySuspend { fixture.getSavedBusinesses.refresh() }
    }

    @Test
    fun `explains a launch business that takes no bookings`() = runUnitTest {
        given()
        val fixture = Fixture()
        everySuspend { fixture.saveBusiness(any()) } throws
            SaveBusiness.Error.AppointmentsNotEnabled()

        whenn()
        val sut = loadedSut(fixture, startParameter = Uuid.random().toString())

        then()
        val message = sut.uiState.notifications.assertSingle<PresentationNotification.Message>()
        assertEquals(
            DashboardRes.strings.dashboard_client_home_save_not_enabled.desc(),
            message.message,
        )
    }

    @Test
    fun `explains the saved businesses limit`() = runUnitTest {
        given()
        val fixture = Fixture()
        everySuspend { fixture.saveBusiness(any()) } throws
            SaveBusiness.Error.SavedBusinessesLimitReached()

        whenn()
        val sut = loadedSut(fixture, startParameter = Uuid.random().toString())

        then()
        val message = sut.uiState.notifications.assertSingle<PresentationNotification.Message>()
        assertEquals(
            DashboardRes.strings.dashboard_client_home_save_limit.desc(),
            message.message,
        )
    }

    @Test
    fun `refreshes saved businesses itself when saving the launch business fails`() = runUnitTest {
        given()
        val fixture = Fixture()
        everySuspend { fixture.saveBusiness(any()) } throws TestException()

        whenn()
        val sut = loadedSut(fixture, startParameter = Uuid.random().toString())

        then()
        verifySuspend { fixture.getSavedBusinesses.refresh() }
        fixture.errorMapper.assertMappedSingle(TestException::class)
        sut.uiState.notifications.assertSingle<PresentationNotification>()
    }

    @Test
    fun `does not refresh saved businesses again after the launch business is saved`() =
        runUnitTest {
            given()
            val fixture = Fixture()

            whenn()
            val sut = loadedSut(fixture, startParameter = Uuid.random().toString())

            then()
            sut.uiState.notifications.assertEmpty()
        }

    @Test
    fun `opens the account deletion confirmation from the launch parameter`() = runUnitTest {
        given()
        val fixture = Fixture()

        whenn()
        val sut = loadedSut(fixture, startParameter = "op_delete-account_$CHALLENGE")

        then()
        assertEquals(
            listOf<ClientHomeDestinations>(
                ClientHomeDestinations.ConfirmAction(ConfirmableAction.DELETE_ACCOUNT),
            ),
            sut.uiState.navigation.navigationDestination,
        )
    }

    @Test
    fun `opens the personal data export confirmation from the launch parameter`() = runUnitTest {
        given()
        val fixture = Fixture()

        whenn()
        val sut = loadedSut(fixture, startParameter = "op_export-personal_$CHALLENGE")

        then()
        assertEquals(
            listOf<ClientHomeDestinations>(
                ClientHomeDestinations.ConfirmAction(ConfirmableAction.EXPORT_PERSONAL_DATA),
            ),
            sut.uiState.navigation.navigationDestination,
        )
    }

    @Test
    fun `loads saved businesses under a launch confirmation`() = runUnitTest {
        given()
        val fixture = Fixture()

        whenn()
        loadedSut(fixture, startParameter = "op_delete-account_$CHALLENGE")

        then()
        verifySuspend { fixture.getSavedBusinesses.refresh() }
        verifySuspend(VerifyMode.exactly(0)) { fixture.resolveBusinessLink(any()) }
        verifySuspend(VerifyMode.exactly(0)) { fixture.saveBusiness(any()) }
    }

    @Test
    fun `explains that an unknown operation link needs a newer app`() = runUnitTest {
        given()
        val fixture = Fixture()

        whenn()
        val sut = loadedSut(fixture, startParameter = "op_transfer-business_$CHALLENGE")

        then()
        val message = sut.uiState.notifications.assertSingle<PresentationNotification.Message>()
        assertEquals(
            DashboardRes.strings.dashboard_client_home_link_unsupported.desc(),
            message.message,
        )
        assertTrue(sut.uiState.navigation.navigationDestination.isEmpty())
        verifySuspend { fixture.getSavedBusinesses.refresh() }
    }

    @Test
    fun `never looks up an operation link as a business link`() = runUnitTest {
        given()
        val fixture = Fixture()

        whenn()
        loadedSut(fixture, startParameter = "op_delete-account_not-a-challenge")

        then()
        verifySuspend(VerifyMode.exactly(0)) { fixture.resolveBusinessLink(any()) }
        verifySuspend(VerifyMode.exactly(0)) { fixture.saveBusiness(any()) }
    }

    private companion object {
        const val CHALLENGE = "0123456789abcdef0123456789abcdef"
    }
}
