package me.bookk.feature.appointments.presentation.screen.history

import dev.mokkery.answering.returns
import dev.mokkery.answering.throws
import dev.mokkery.everySuspend
import dev.mokkery.matcher.any
import dev.mokkery.mock
import dev.mokkery.verify.VerifyMode
import dev.mokkery.verifySuspend
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.advanceUntilIdle
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
import me.bookk.designsystem.test.assertMappedSingle
import me.bookk.feature.appointments.domain.api.GetAppointmentHistory
import me.bookk.feature.appointments.domain.api.entity.Appointment
import me.bookk.feature.appointments.presentation.FakeAppointmentsStateFactory
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
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
        val getAppointmentHistory = mock<GetAppointmentHistory> {
            everySuspend { reload(any(), any()) } returns emptyList()
        }
        val errorMapper = FakeErrorMapper()

        fun sut() = AppointmentHistoryViewModel(
            businessId = businessId,
            getAppointmentHistory = getAppointmentHistory,
            dateLocalizer = FakeDateLocalizer(),
            stateFactory = FakeAppointmentsStateFactory(),
            vmArgs = VmArgs(errorMapper)
        )
    }

    @Test
    fun `loads first page without query on start`() = runUnitTest {
        given()
        val fixture = Fixture()
        val appointment = Appointment.stub()
        everySuspend { fixture.getAppointmentHistory.reload(any(), any()) } returns listOf(appointment)

        whenn()
        val sut = fixture.sut()

        then()
        verifySuspend { fixture.getAppointmentHistory.reload(fixture.businessId, null) }
        assertEquals(listOf(appointment.id), sut.uiState.appointments.items.map { it.id })
        assertFalse(sut.uiState.appointments.isInitialLoading)
    }

    @Test
    fun `appends next page on load more`() = runUnitTest {
        given()
        val fixture = Fixture()
        val first = Appointment.stub()
        val second = Appointment.stub()
        everySuspend { fixture.getAppointmentHistory.reload(any(), any()) } returns listOf(first)
        everySuspend { fixture.getAppointmentHistory.loadMore() } returns listOf(second)
        val sut = fixture.sut()

        whenn()
        sut.uiState.appointments.loadMore?.invoke()

        then()
        assertEquals(listOf(first.id, second.id), sut.uiState.appointments.items.map { it.id })
    }

    @Test
    fun `searches by trimmed query after debounce`() = runUnitTest {
        given()
        val fixture = Fixture()
        val sut = fixture.sut()

        whenn()
        (sut.uiState.searchField as FakeTextFieldState).type("  anna ")
        advanceUntilIdle()

        then()
        verifySuspend { fixture.getAppointmentHistory.reload(fixture.businessId, "anna") }
    }

    @Test
    fun `does not search before debounce elapses`() = runUnitTest {
        given()
        val fixture = Fixture()
        val sut = fixture.sut()

        whenn()
        (sut.uiState.searchField as FakeTextFieldState).type("anna")
        advanceTimeBy(100)

        then()
        verifySuspend(VerifyMode.not) { fixture.getAppointmentHistory.reload(any(), "anna") }
    }

    @Test
    fun `refresh reloads with current query and stops refreshing`() = runUnitTest {
        given()
        val fixture = Fixture()
        val sut = fixture.sut()
        sut.uiState.searchField.text = "anna"

        whenn()
        sut.uiState.refresh.onRefresh()

        then()
        verifySuspend { fixture.getAppointmentHistory.reload(fixture.businessId, "anna") }
        assertFalse(sut.uiState.refresh.isRefreshing)
    }

    @Test
    fun `navigates to details on item click`() = runUnitTest {
        given()
        val fixture = Fixture()
        val appointment = Appointment.stub()
        everySuspend { fixture.getAppointmentHistory.reload(any(), any()) } returns listOf(appointment)
        val sut = fixture.sut()

        whenn()
        sut.uiState.appointments.items.single().onItemClick()

        then()
        assertEquals(listOf<AppointmentHistoryDestinations>(AppointmentHistoryDestinations.AppointmentDetails(appointment.id)), sut.uiState.navigation.navigationDestination)
    }

    @Test
    fun `shows mapped error when history cannot be loaded`() = runUnitTest {
        given()
        val fixture = Fixture()
        everySuspend { fixture.getAppointmentHistory.reload(any(), any()) } throws TestException()

        whenn()
        val sut = fixture.sut()

        then()
        fixture.errorMapper.assertMappedSingle(TestException::class)
        assertFalse(sut.uiState.appointments.isInitialLoading)
    }

    @Test
    fun `pushes back destination on back click`() = runUnitTest {
        given()
        val sut = Fixture().sut()

        whenn()
        sut.uiState.appBar.onBackClick?.invoke()

        then()
        assertEquals(listOf<AppointmentHistoryDestinations>(AppointmentHistoryDestinations.Back), sut.uiState.navigation.navigationDestination)
    }
}
