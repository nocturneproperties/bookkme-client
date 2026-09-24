package me.bookk.feature.appointments.presentation.screen.list

import dev.icerock.moko.resources.format
import dev.mokkery.answering.returns
import dev.mokkery.answering.throws
import dev.mokkery.every
import dev.mokkery.everySuspend
import dev.mokkery.matcher.any
import dev.mokkery.mock
import dev.mokkery.verify.VerifyMode
import dev.mokkery.verifySuspend
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.plus
import me.bookk.android.feature.appointments.resources.AppointmentsRes
import me.bookk.core.presentation.VmArgs
import me.bookk.core.presentation.date.today
import me.bookk.core.test.given
import me.bookk.core.test.runUnitTest
import me.bookk.core.test.whenn
import me.bookk.designsystem.test.FakeDateLocalizer
import me.bookk.designsystem.test.FakeErrorMapper
import me.bookk.designsystem.test.TestException
import me.bookk.designsystem.test.ViewModelTestDispatchers
import me.bookk.designsystem.test.assertEmpty
import me.bookk.designsystem.test.assertMappedSingle
import me.bookk.designsystem.test.failOnceThenSuspend
import me.bookk.feature.appointments.domain.api.GetAppointmentRequests
import me.bookk.feature.appointments.domain.api.GetAppointmentsForBusiness
import me.bookk.feature.appointments.domain.api.ObserveCurrentBusinessId
import me.bookk.feature.appointments.domain.api.entity.Appointment
import me.bookk.feature.appointments.domain.api.entity.AppointmentEvent
import me.bookk.feature.appointments.domain.api.entity.AppointmentRequest
import me.bookk.feature.appointments.domain.api.entity.appointmentEvents
import me.bookk.feature.appointments.presentation.FakeAppointmentsStateFactory
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlin.uuid.Uuid

class AppointmentListViewModelTest {

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
        val observeCurrentBusinessId = mock<ObserveCurrentBusinessId> {
            every { invoke() } returns MutableStateFlow<Uuid?>(businessId)
        }
        val getAppointmentsForBusiness = mock<GetAppointmentsForBusiness> {
            every { flow(any()) } returns appointments
            everySuspend { refresh(any(), any()) } returns emptyList()
        }
        val getAppointmentRequests = mock<GetAppointmentRequests> {
            everySuspend { refresh(any()) } returns emptyList()
        }
        val errorMapper = FakeErrorMapper()

        fun sut() = AppointmentListViewModel(
            observeCurrentBusinessId = observeCurrentBusinessId,
            getAppointmentsForBusiness = getAppointmentsForBusiness,
            getAppointmentRequests = getAppointmentRequests,
            dateLocalizer = FakeDateLocalizer(),
            stateFactory = FakeAppointmentsStateFactory(),
            vmArgs = VmArgs(errorMapper)
        )
    }

    @Test
    fun `refreshes todays appointments of current business on start`() = runUnitTest {
        given()
        val fixture = Fixture()

        whenn()
        fixture.sut()

        then()
        verifySuspend { fixture.getAppointmentsForBusiness.refresh(fixture.businessId, LocalDate.today()) }
    }

    @Test
    fun `shows pending request count`() = runUnitTest {
        given()
        val fixture = Fixture()
        everySuspend { fixture.getAppointmentRequests.refresh(any()) } returns List(3) { AppointmentRequest.stub() }

        whenn()
        val sut = fixture.sut()

        then()
        assertEquals(AppointmentsRes.strings.appointments_requests_count.format(3), sut.uiState.requestsButton.text)
    }

    @Test
    fun `ignores request count failure`() = runUnitTest {
        given()
        val fixture = Fixture()
        everySuspend { fixture.getAppointmentRequests.refresh(any()) } throws TestException()

        whenn()
        val sut = fixture.sut()

        then()
        sut.uiState.notifications.assertEmpty()
    }

    @Test
    fun `renders cached appointments`() = runUnitTest {
        given()
        val fixture = Fixture()
        val appointment = Appointment.stub(date = LocalDateTime(2030, 1, 1, 10, 0))
        val sut = fixture.sut()
        advanceUntilIdle()

        whenn()
        fixture.appointments.value = listOf(appointment)

        then()
        val item = sut.uiState.appointments.items.single()
        assertEquals(appointment.client.fullName, item.clientName)
        assertEquals("10:00", item.scheduledAt)
    }

    @Test
    fun `loads appointments for newly picked date`() = runUnitTest {
        given()
        val fixture = Fixture()
        val tomorrow = LocalDate.today().plus(1, DateTimeUnit.DAY)
        val sut = fixture.sut()

        whenn()
        sut.uiState.datePicker.onDatePicked?.invoke(tomorrow)

        then()
        verifySuspend { fixture.getAppointmentsForBusiness.refresh(fixture.businessId, tomorrow) }
        assertEquals(tomorrow, sut.uiState.datePicker.pickedDate)
        assertEquals(7, sut.uiState.dates.items.size)
        assertTrue(sut.uiState.dates.items.any { it.date == tomorrow })
    }

    @Test
    fun `does not reload when the same date is picked`() = runUnitTest {
        given()
        val fixture = Fixture()
        val sut = fixture.sut()

        whenn()
        sut.uiState.datePicker.onDatePicked?.invoke(LocalDate.today())

        then()
        verifySuspend(VerifyMode.exactly(1)) { fixture.getAppointmentsForBusiness.refresh(any(), any()) }
    }

    @Test
    fun `marks today in week strip`() = runUnitTest {
        given()
        val fixture = Fixture()

        whenn()
        val sut = fixture.sut()

        then()
        assertEquals(LocalDate.today(), sut.uiState.dates.items.single { it.isToday }.date)
    }

    @Test
    fun `reloads when an appointment is created`() = runUnitTest {
        given()
        val fixture = Fixture()
        fixture.sut()

        whenn()
        launch(Dispatchers.Unconfined) { appointmentEvents.emit(AppointmentEvent.Created(Appointment.stub())) }

        then()
        verifySuspend(VerifyMode.exactly(2)) { fixture.getAppointmentsForBusiness.refresh(fixture.businessId, LocalDate.today()) }
    }

    @Test
    fun `reloads when an appointment is updated`() = runUnitTest {
        given()
        val fixture = Fixture()
        fixture.sut()

        whenn()
        launch(Dispatchers.Unconfined) { appointmentEvents.emit(AppointmentEvent.Updated(Appointment.stub())) }

        then()
        verifySuspend(VerifyMode.exactly(2)) { fixture.getAppointmentRequests.refresh(fixture.businessId) }
    }

    @Test
    fun `navigates to create appointment for current business`() = runUnitTest {
        given()
        val fixture = Fixture()
        val sut = fixture.sut()

        whenn()
        sut.uiState.appBar.actions.items[1].onClick()

        then()
        assertEquals(listOf<AppointmentListDestinations>(AppointmentListDestinations.CreateAppointment(fixture.businessId)), sut.uiState.navigation.navigationDestination)
    }

    @Test
    fun `opens date picker from app bar`() = runUnitTest {
        given()
        val sut = Fixture().sut()

        whenn()
        sut.uiState.appBar.actions.items[0].onClick()

        then()
        assertTrue(sut.uiState.datePicker.isDatePickerVisible)
    }

    @Test
    fun `opens requests on requests button click`() = runUnitTest {
        given()
        val sut = Fixture().sut()

        whenn()
        sut.uiState.requestsButton.onClick?.invoke()

        then()
        assertTrue(sut.uiState.isRequestsVisible)
    }

    @Test
    fun `navigates to appointment details on item click`() = runUnitTest {
        given()
        val fixture = Fixture()
        val appointment = Appointment.stub()
        val sut = fixture.sut()
        advanceUntilIdle()
        fixture.appointments.value = listOf(appointment)

        whenn()
        sut.uiState.appointments.items.single().onItemClick()

        then()
        assertEquals(listOf<AppointmentListDestinations>(AppointmentListDestinations.AppointmentDetails(appointment.id)), sut.uiState.navigation.navigationDestination)
    }

    @Test
    fun `shows mapped error when appointment observation fails`() = runUnitTest {
        given()
        val fixture = Fixture()
        every { fixture.getAppointmentsForBusiness.flow(any()) } returns failOnceThenSuspend()

        whenn()
        fixture.sut()

        then()
        fixture.errorMapper.assertMappedSingle(TestException::class)
    }
}
