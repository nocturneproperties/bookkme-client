package me.bookk.feature.appointments.presentation.screen.history

import dev.mokkery.answering.calls
import dev.mokkery.answering.returns
import dev.mokkery.answering.throws
import dev.mokkery.every
import dev.mokkery.everySuspend
import dev.mokkery.matcher.any
import dev.mokkery.mock
import dev.mokkery.verify.VerifyMode
import dev.mokkery.verifySuspend
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.advanceUntilIdle
import library.money.api.Money
import me.bookk.core.domain.pagination.Page
import me.bookk.core.presentation.VmArgs
import me.bookk.core.test.given
import me.bookk.core.test.runUnitTest
import me.bookk.core.test.then
import me.bookk.core.test.whenn
import me.bookk.designsystem.test.FakeDateLocalizer
import me.bookk.designsystem.test.FakeErrorMapper
import me.bookk.designsystem.test.FakeTextFieldState
import me.bookk.designsystem.test.TestException
import me.bookk.designsystem.test.ViewModelTestDispatchers
import me.bookk.feature.appointments.domain.api.GetAppointmentHistory
import me.bookk.feature.appointments.domain.api.entity.Appointment
import me.bookk.feature.appointments.domain.api.entity.AppointmentHistoryQuery
import me.bookk.feature.appointments.domain.api.entity.ClientSnapshot
import me.bookk.feature.appointments.domain.api.entity.PriceAdjustment
import me.bookk.feature.appointments.domain.api.entity.ServiceSnapshot
import me.bookk.feature.appointments.presentation.FakeAppointmentsStateFactory
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertTrue
import kotlin.uuid.Uuid

class AppointmentHistoryViewModelTest {

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
        val businessId = Uuid.random()
        val appointments = MutableStateFlow<List<Appointment>>(emptyList())
        val getAppointmentHistory = mock<GetAppointmentHistory> {
            every { flow(any(), any()) } calls { (_: Uuid, search: String?) ->
                appointments.matching(search)
            }
            everySuspend { refresh(any()) } returns Page(emptyList(), nextCursor = null)
        }
        val errorMapper = FakeErrorMapper()

        fun sut() = AppointmentHistoryViewModel(
            businessId = businessId,
            getAppointmentHistory = getAppointmentHistory,
            dateLocalizer = FakeDateLocalizer(),
            stateFactory = FakeAppointmentsStateFactory(),
            vmArgs = VmArgs(errorMapper),
        )
    }

    @Test
    fun `loads the first page of the business history on start`() = runUnitTest {
        given()
        val fixture = Fixture()

        whenn()
        fixture.sut()

        then()
        verifySuspend {
            fixture.getAppointmentHistory.refresh(AppointmentHistoryQuery(fixture.businessId))
        }
    }

    @Test
    fun `lists the appointments the history flow emits`() = runUnitTest {
        given()
        val fixture = Fixture()
        val newer = Appointment.stub()
        val older = Appointment.stub()
        val sut = fixture.sut()
        advanceUntilIdle()

        whenn()
        fixture.appointments.value = listOf(newer, older)

        then()
        assertEquals(listOf(newer.id, older.id), sut.uiState.appointments.items.map { it.id })
    }

    @Test
    fun `offers more appointments when the first page has more`() = runUnitTest {
        given()
        val fixture = Fixture()
        everySuspend { fixture.getAppointmentHistory.refresh(any()) } returns
            Page(listOf(Appointment.stub()), nextCursor = "after-1")

        whenn()
        val sut = fixture.sut()
        advanceUntilIdle()

        then()
        assertNotNull(sut.uiState.appointments.loadMore)
    }

    @Test
    fun `loads the next page of the current search at the end of the list`() = runUnitTest {
        given()
        val fixture = Fixture()
        everySuspend { fixture.getAppointmentHistory.refresh(any()) } returns
            Page(listOf(Appointment.stub()), nextCursor = "after-1")
        everySuspend { fixture.getAppointmentHistory.loadMore(any(), any()) } returns
            Page(emptyList(), nextCursor = null)
        val sut = fixture.sut()
        (sut.uiState.searchField as FakeTextFieldState).type("anna")
        advanceUntilIdle()

        whenn()
        sut.uiState.appointments.loadMore?.invoke()

        then()
        verifySuspend {
            fixture.getAppointmentHistory.loadMore(
                AppointmentHistoryQuery(fixture.businessId, "anna"),
                "after-1",
            )
        }
    }

    @Test
    fun `shows the appointments the flow finds for the typed search at once`() = runUnitTest {
        given()
        val fixture = Fixture()
        val anna = Appointment.stub().copy(client = ClientSnapshot.stub().copy(fullName = "Anna"))
        val olga = Appointment.stub().copy(client = ClientSnapshot.stub().copy(fullName = "Olga"))
        val sut = fixture.sut()
        advanceUntilIdle()
        fixture.appointments.value = listOf(anna, olga)

        whenn()
        (sut.uiState.searchField as FakeTextFieldState).type("ann")

        then()
        assertEquals(listOf(anna.id), sut.uiState.appointments.items.map { it.id })
    }

    @Test
    fun `searches the server by the trimmed query after a pause`() = runUnitTest {
        given()
        val fixture = Fixture()
        val sut = fixture.sut()

        whenn()
        (sut.uiState.searchField as FakeTextFieldState).type("  anna ")
        advanceUntilIdle()

        then()
        verifySuspend {
            fixture.getAppointmentHistory.refresh(
                AppointmentHistoryQuery(fixture.businessId, "anna"),
            )
        }
    }

    @Test
    fun `does not search the server before the pause elapses`() = runUnitTest {
        given()
        val fixture = Fixture()
        val sut = fixture.sut()

        whenn()
        (sut.uiState.searchField as FakeTextFieldState).type("anna")
        advanceTimeBy(100)

        then()
        verifySuspend(VerifyMode.not) {
            fixture.getAppointmentHistory.refresh(
                AppointmentHistoryQuery(fixture.businessId, "anna"),
            )
        }
    }

    @Test
    fun `pull to refresh reloads the first page of the current search`() = runUnitTest {
        given()
        val fixture = Fixture()
        val sut = fixture.sut()
        (sut.uiState.searchField as FakeTextFieldState).type("anna")
        advanceUntilIdle()

        whenn()
        sut.uiState.refresh.onRefresh()
        advanceUntilIdle()

        then()
        verifySuspend(VerifyMode.exactly(2)) {
            fixture.getAppointmentHistory.refresh(
                AppointmentHistoryQuery(fixture.businessId, "anna"),
            )
        }
        assertFalse(sut.uiState.refresh.isRefreshing)
    }

    @Test
    fun `shows adjusted services and price for an adjusted appointment`() = runUnitTest {
        given()
        val fixture = Fixture()
        val appointment = Appointment.stub().copy(
            priceAdjustment = PriceAdjustment(
                additionalServices = listOf(ServiceSnapshot.stub().copy(name = "Coloring")),
                price = Money(250.0, Money.SupportedCurrency.USD),
                reason = null,
            ),
        )
        val sut = fixture.sut()
        advanceUntilIdle()

        whenn()
        fixture.appointments.value = listOf(appointment)

        then()
        val item = sut.uiState.appointments.items.single()
        assertEquals("Coloring", item.serviceName)
        assertEquals(Money(250.0, Money.SupportedCurrency.USD).toString(), item.earnings)
    }

    @Test
    fun `navigates to details on item click`() = runUnitTest {
        given()
        val fixture = Fixture()
        val appointment = Appointment.stub()
        val sut = fixture.sut()
        advanceUntilIdle()
        fixture.appointments.value = listOf(appointment)

        whenn()
        sut.uiState.appointments.items.single().onItemClick()

        then()
        assertEquals(
            listOf<AppointmentHistoryDestinations>(
                AppointmentHistoryDestinations.AppointmentDetails(appointment.id),
            ),
            sut.uiState.navigation.navigationDestination,
        )
    }

    @Test
    fun `shows a full screen error when the first page cannot be loaded`() = runUnitTest {
        given()
        val fixture = Fixture()
        everySuspend { fixture.getAppointmentHistory.refresh(any()) } throws TestException()

        whenn()
        val sut = fixture.sut()

        then()
        assertNotNull(sut.uiState.appointments.errorState)
        assertTrue(sut.uiState.appointments.items.isEmpty())
    }

    @Test
    fun `pushes back destination on back click`() = runUnitTest {
        given()
        val sut = Fixture().sut()

        whenn()
        sut.uiState.appBar.onBackClick?.invoke()

        then()
        assertEquals(
            listOf<AppointmentHistoryDestinations>(AppointmentHistoryDestinations.Back),
            sut.uiState.navigation.navigationDestination,
        )
    }
}

private fun MutableStateFlow<List<Appointment>>.matching(search: String?): Flow<List<Appointment>> {
    return map { appointments ->
        appointments.filter {
            search.isNullOrBlank() || it.client.fullName.contains(search.trim(), ignoreCase = true)
        }
    }
}
