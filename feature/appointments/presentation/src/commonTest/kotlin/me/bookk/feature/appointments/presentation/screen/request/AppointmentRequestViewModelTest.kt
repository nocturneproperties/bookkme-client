package me.bookk.feature.appointments.presentation.screen.request

import dev.icerock.moko.resources.desc.desc
import dev.icerock.moko.resources.format
import dev.mokkery.answering.returns
import dev.mokkery.answering.throws
import dev.mokkery.every
import dev.mokkery.everySuspend
import dev.mokkery.matcher.any
import dev.mokkery.mock
import dev.mokkery.verify
import dev.mokkery.verifySuspend
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant
import library.device.api.DeviceFacade
import library.money.api.Money
import me.bookk.android.feature.appointments.resources.AppointmentsRes
import me.bookk.core.presentation.VmArgs
import me.bookk.core.presentation.error.PresentationNotification
import me.bookk.core.test.given
import me.bookk.core.test.runUnitTest
import me.bookk.core.test.then
import me.bookk.core.test.whenn
import me.bookk.designsystem.resources.toStringDesc
import me.bookk.designsystem.test.FakeDateLocalizer
import me.bookk.designsystem.test.FakeErrorMapper
import me.bookk.designsystem.test.TestException
import me.bookk.designsystem.test.ViewModelTestDispatchers
import me.bookk.designsystem.test.assertMappedSingle
import me.bookk.designsystem.test.assertSingle
import me.bookk.designsystem.test.failOnceThenSuspend
import me.bookk.feature.appointments.domain.api.ApproveAppointmentRequest
import me.bookk.feature.appointments.domain.api.DeclineAppointmentRequest
import me.bookk.feature.appointments.domain.api.GetAssignedAppointmentRequests
import me.bookk.feature.appointments.domain.api.ObserveCurrentBusinessId
import me.bookk.feature.appointments.domain.api.entity.Appointment
import me.bookk.feature.appointments.domain.api.entity.AppointmentRequest
import me.bookk.feature.appointments.domain.api.entity.ClientSnapshot
import me.bookk.feature.appointments.domain.api.entity.ServiceSnapshot
import me.bookk.feature.appointments.presentation.FakeAppointmentsStateFactory
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue
import kotlin.time.Duration.Companion.minutes
import kotlin.uuid.Uuid

class AppointmentRequestViewModelTest {

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
        val requests = MutableStateFlow<List<AppointmentRequest>>(emptyList())
        val getAssignedAppointmentRequests = mock<GetAssignedAppointmentRequests> {
            every { flow(any()) } returns requests
            everySuspend { refresh(any()) } returns emptyList()
        }
        val approveAppointmentRequest = mock<ApproveAppointmentRequest>()
        val declineAppointmentRequest = mock<DeclineAppointmentRequest>()
        val observeCurrentBusinessId = mock<ObserveCurrentBusinessId> {
            every { invoke() } returns MutableStateFlow<Uuid?>(businessId)
        }
        val device = mock<DeviceFacade> {
            every { dial(any()) } returns Unit
        }
        val errorMapper = FakeErrorMapper()

        fun sut() = AppointmentRequestViewModel(
            getAssignedAppointmentRequests = getAssignedAppointmentRequests,
            approveAppointmentRequest = approveAppointmentRequest,
            declineAppointmentRequest = declineAppointmentRequest,
            observeCurrentBusinessId = observeCurrentBusinessId,
            device = device,
            stateFactory = FakeAppointmentsStateFactory(),
            dateLocalizer = FakeDateLocalizer(),
            vmArgs = VmArgs(errorMapper),
        )
    }

    private suspend fun kotlinx.coroutines.test.TestScope.presentedSutWith(
        fixture: Fixture,
        vararg requests: AppointmentRequest,
    ): AppointmentRequestViewModel {
        val sut = fixture.sut()
        sut.onViewPresented()
        advanceUntilIdle()
        fixture.requests.value = requests.toList()
        return sut
    }

    @Test
    fun `refreshes requests of current business when presented`() = runUnitTest {
        given()
        val fixture = Fixture()
        val sut = fixture.sut()

        whenn()
        sut.onViewPresented()

        then()
        verifySuspend { fixture.getAssignedAppointmentRequests.refresh(fixture.businessId) }
    }

    private fun service(name: String, minutes: Int): ServiceSnapshot {
        return ServiceSnapshot(
            Uuid.random(),
            name,
            Uuid.random(),
            Money(1000L, Money.SupportedCurrency.UAH),
            minutes.minutes,
        )
    }

    private fun client(fullName: String = "Anna Kowalska", phone: String? = "+48501234567") =
        ClientSnapshot.stub().copy(fullName = fullName, phone = phone)

    @Test
    fun `renders client name and note`() = runUnitTest {
        given()
        val fixture = Fixture()
        val request = AppointmentRequest.stub().copy(client = client(), note = "Window")

        whenn()
        val sut = presentedSutWith(fixture, request)

        then()
        val item = sut.uiState.requests.items.single()
        assertEquals(request.id.toString(), item.id)
        assertEquals("Anna Kowalska", item.clientName)
        assertEquals("Window", item.note)
    }

    @Test
    fun `renders initials of first and last name`() = runUnitTest {
        given()
        val fixture = Fixture()
        val request = AppointmentRequest.stub().copy(client = client("anna maria kowalska"))

        whenn()
        val sut = presentedSutWith(fixture, request)

        then()
        assertEquals("AK", sut.uiState.requests.items.single().clientInitials)
    }

    @Test
    fun `renders single initial when client has one name`() = runUnitTest {
        given()
        val fixture = Fixture()
        val request = AppointmentRequest.stub().copy(client = client(" Anna "))

        whenn()
        val sut = presentedSutWith(fixture, request)

        then()
        assertEquals("A", sut.uiState.requests.items.single().clientInitials)
    }

    @Test
    fun `renders date with start and end time of all services`() = runUnitTest {
        given()
        val fixture = Fixture()
        val start = LocalDateTime(2030, 1, 2, 14, 0)
        val request = AppointmentRequest.stub().copy(
            date = start.toInstant(TimeZone.currentSystemDefault()),
            services = listOf(service("Haircut", 45), service("Coloring", 90)),
        )

        whenn()
        val sut = presentedSutWith(fixture, request)

        then()
        assertEquals(
            AppointmentsRes.strings.appointments_request_schedule.format(
                "2030-01-02",
                "14:00",
                "16:15",
            ),
            sut.uiState.requests.items.single().schedule,
        )
    }

    @Test
    fun `renders each service with its duration`() = runUnitTest {
        given()
        val fixture = Fixture()
        val request = AppointmentRequest.stub().copy(
            services = listOf(service("Haircut", 45), service("Coloring", 90)),
        )

        whenn()
        val sut = presentedSutWith(fixture, request)

        then()
        assertEquals(
            listOf(
                AppointmentsRes.strings.appointments_request_service.format(
                    "Haircut",
                    45.minutes.toStringDesc(),
                ),
                AppointmentsRes.strings.appointments_request_service.format(
                    "Coloring",
                    90.minutes.toStringDesc(),
                ),
            ),
            sut.uiState.requests.items.single().services,
        )
    }

    @Test
    fun `renders total price of all services`() = runUnitTest {
        given()
        val fixture = Fixture()
        val request = AppointmentRequest.stub().copy(
            services = listOf(service("Haircut", 45), service("Coloring", 90)),
        )

        whenn()
        val sut = presentedSutWith(fixture, request)

        then()
        assertEquals(request.total, sut.uiState.requests.items.single().price)
    }

    @Test
    fun `dials client phone when call is clicked`() = runUnitTest {
        given()
        val fixture = Fixture()
        val request = AppointmentRequest.stub().copy(client = client(phone = "+48501234567"))
        val sut = presentedSutWith(fixture, request)

        whenn()
        sut.uiState.requests.items.single().onCallClick?.invoke()

        then()
        verify { fixture.device.dial("+48501234567") }
    }

    @Test
    fun `hides call action when client has no phone`() = runUnitTest {
        given()
        val fixture = Fixture()
        val request = AppointmentRequest.stub().copy(client = client(phone = null))

        whenn()
        val sut = presentedSutWith(fixture, request)

        then()
        assertNull(sut.uiState.requests.items.single().onCallClick)
    }

    @Test
    fun `hides call action when client phone is blank`() = runUnitTest {
        given()
        val fixture = Fixture()
        val request = AppointmentRequest.stub().copy(client = client(phone = " "))

        whenn()
        val sut = presentedSutWith(fixture, request)

        then()
        assertNull(sut.uiState.requests.items.single().onCallClick)
    }

    @Test
    fun `shows requests title`() = runUnitTest {
        given()
        val fixture = Fixture()

        whenn()
        val sut = presentedSutWith(fixture, AppointmentRequest.stub(), AppointmentRequest.stub())

        then()
        assertEquals(AppointmentsRes.strings.appointments_requests.desc(), sut.uiState.title)
    }

    @Test
    fun `removes request after approval`() = runUnitTest {
        given()
        val fixture = Fixture()
        val request = AppointmentRequest.stub()
        everySuspend { fixture.approveAppointmentRequest(any()) } returns Appointment.stub()
        val sut = presentedSutWith(fixture, request)

        whenn()
        sut.uiState.requests.items.single().approveButton.onClick?.invoke()

        then()
        verifySuspend { fixture.approveAppointmentRequest(request.id) }
        assertTrue(sut.uiState.requests.items.isEmpty())
    }

    @Test
    fun `plays success haptic when request is approved`() = runUnitTest {
        given()
        val fixture = Fixture()
        val request = AppointmentRequest.stub()
        everySuspend { fixture.approveAppointmentRequest(any()) } returns Appointment.stub()
        val sut = presentedSutWith(fixture, request)

        whenn()
        sut.uiState.requests.items.single().approveButton.onClick?.invoke()

        then()
        assertEquals(
            1,
            sut.uiState.notifications.presentationNotification.count {
                it is PresentationNotification.SuccessHaptic
            },
        )
    }

    @Test
    fun `does not play success haptic when approval fails`() = runUnitTest {
        given()
        val fixture = Fixture()
        everySuspend { fixture.approveAppointmentRequest(any()) } throws TestException()
        val sut = presentedSutWith(fixture, AppointmentRequest.stub())

        whenn()
        sut.uiState.requests.items.single().approveButton.onClick?.invoke()

        then()
        assertTrue(
            sut.uiState.notifications.presentationNotification.none {
                it is PresentationNotification.SuccessHaptic
            },
        )
    }

    @Test
    fun `shows message when approved slot overlaps`() = runUnitTest {
        given()
        val fixture = Fixture()
        everySuspend { fixture.approveAppointmentRequest(any()) } throws
            ApproveAppointmentRequest.Error.AppointmentExists()
        val sut = presentedSutWith(fixture, AppointmentRequest.stub())

        whenn()
        sut.uiState.requests.items.single().approveButton.onClick?.invoke()

        then()
        sut.uiState.notifications.assertSingle<PresentationNotification.Message>()
        assertEquals(1, sut.uiState.requests.items.size)
        assertFalse(sut.uiState.requests.items.single().approveButton.isLoading)
    }

    @Test
    fun `shows message when request is in the past`() = runUnitTest {
        given()
        val fixture = Fixture()
        everySuspend { fixture.approveAppointmentRequest(any()) } throws
            ApproveAppointmentRequest.Error.DateInPast()
        val sut = presentedSutWith(fixture, AppointmentRequest.stub())

        whenn()
        sut.uiState.requests.items.single().approveButton.onClick?.invoke()

        then()
        sut.uiState.notifications.assertSingle<PresentationNotification.Message>()
    }

    @Test
    fun `shows message when requested date is not allowed`() = runUnitTest {
        given()
        val fixture = Fixture()
        everySuspend { fixture.approveAppointmentRequest(any()) } throws
            ApproveAppointmentRequest.Error.DateNotAllowed()
        val sut = presentedSutWith(fixture, AppointmentRequest.stub())

        whenn()
        sut.uiState.requests.items.single().approveButton.onClick?.invoke()

        then()
        sut.uiState.notifications.assertSingle<PresentationNotification.Message>()
    }

    @Test
    fun `shows message when requested time is not allowed`() = runUnitTest {
        given()
        val fixture = Fixture()
        everySuspend { fixture.approveAppointmentRequest(any()) } throws
            ApproveAppointmentRequest.Error.TimeNotAllowed()
        val sut = presentedSutWith(fixture, AppointmentRequest.stub())

        whenn()
        sut.uiState.requests.items.single().approveButton.onClick?.invoke()

        then()
        sut.uiState.notifications.assertSingle<PresentationNotification.Message>()
    }

    @Test
    fun `shows employee day message when employee does not work on requested date`() = runUnitTest {
        given()
        val fixture = Fixture()
        everySuspend { fixture.approveAppointmentRequest(any()) } throws
            ApproveAppointmentRequest.Error.EmployeeDateNotAllowed()
        val sut = presentedSutWith(fixture, AppointmentRequest.stub())

        whenn()
        sut.uiState.requests.items.single().approveButton.onClick?.invoke()

        then()
        val message = sut.uiState.notifications.assertSingle<PresentationNotification.Message>()
        assertEquals(
            AppointmentsRes.strings.appointments_employee_date_error.desc(),
            message.message,
        )
        assertTrue(fixture.errorMapper.mappedErrors.isEmpty())
    }

    @Test
    fun `shows employee hours message when requested time is outside employee hours`() =
        runUnitTest {
            given()
            val fixture = Fixture()
            everySuspend { fixture.approveAppointmentRequest(any()) } throws
                ApproveAppointmentRequest.Error.EmployeeTimeNotAllowed()
            val sut = presentedSutWith(fixture, AppointmentRequest.stub())

            whenn()
            sut.uiState.requests.items.single().approveButton.onClick?.invoke()

            then()
            val message = sut.uiState.notifications.assertSingle<PresentationNotification.Message>()
            assertEquals(
                AppointmentsRes.strings.appointments_employee_time_error.desc(),
                message.message,
            )
            assertTrue(fixture.errorMapper.mappedErrors.isEmpty())
        }

    @Test
    fun `shows suspended message when requested employee is suspended`() = runUnitTest {
        given()
        val fixture = Fixture()
        everySuspend { fixture.approveAppointmentRequest(any()) } throws
            ApproveAppointmentRequest.Error.EmployeeSuspended()
        val sut = presentedSutWith(fixture, AppointmentRequest.stub())

        whenn()
        sut.uiState.requests.items.single().approveButton.onClick?.invoke()

        then()
        val message = sut.uiState.notifications.assertSingle<PresentationNotification.Message>()
        assertEquals(
            AppointmentsRes.strings.appointments_details_employee_suspended_error.desc(),
            message.message,
        )
        assertTrue(fixture.errorMapper.mappedErrors.isEmpty())
    }

    @Test
    fun `shows not found message when requested employee was removed`() = runUnitTest {
        given()
        val fixture = Fixture()
        everySuspend { fixture.approveAppointmentRequest(any()) } throws
            ApproveAppointmentRequest.Error.EmployeeNotFound()
        val sut = presentedSutWith(fixture, AppointmentRequest.stub())

        whenn()
        sut.uiState.requests.items.single().approveButton.onClick?.invoke()

        then()
        val message = sut.uiState.notifications.assertSingle<PresentationNotification.Message>()
        assertEquals(
            AppointmentsRes.strings.appointments_employee_not_found_error.desc(),
            message.message,
        )
        assertTrue(fixture.errorMapper.mappedErrors.isEmpty())
    }

    @Test
    fun `shows mapped error when approval fails unexpectedly`() = runUnitTest {
        given()
        val fixture = Fixture()
        everySuspend { fixture.approveAppointmentRequest(any()) } throws TestException()
        val sut = presentedSutWith(fixture, AppointmentRequest.stub())

        whenn()
        sut.uiState.requests.items.single().approveButton.onClick?.invoke()

        then()
        fixture.errorMapper.assertMappedSingle(TestException::class)
    }

    @Test
    fun `declines request with entered reason and removes it`() = runUnitTest {
        given()
        val fixture = Fixture()
        val request = AppointmentRequest.stub()
        everySuspend { fixture.declineAppointmentRequest(any(), any(), any()) } returns Unit
        val sut = presentedSutWith(fixture, request)
        sut.uiState.requests.items.single().declineButton.onClick?.invoke()
        val dialog = sut.uiState.notifications.assertSingle<PresentationNotification.InputMessage>()

        whenn()
        dialog.onConfirm("Fully booked")

        then()
        verifySuspend {
            fixture.declineAppointmentRequest(request.id, fixture.businessId, "Fully booked")
        }
        assertTrue(sut.uiState.requests.items.isEmpty())
    }

    @Test
    fun `plays success haptic when request is declined`() = runUnitTest {
        given()
        val fixture = Fixture()
        val request = AppointmentRequest.stub()
        everySuspend { fixture.declineAppointmentRequest(any(), any(), any()) } returns Unit
        val sut = presentedSutWith(fixture, request)
        sut.uiState.requests.items.single().declineButton.onClick?.invoke()
        val dialog = sut.uiState.notifications.assertSingle<PresentationNotification.InputMessage>()

        whenn()
        dialog.onConfirm("Fully booked")

        then()
        assertEquals(
            1,
            sut.uiState.notifications.presentationNotification.count {
                it is PresentationNotification.SuccessHaptic
            },
        )
    }

    @Test
    fun `does not play success haptic when decline fails`() = runUnitTest {
        given()
        val fixture = Fixture()
        everySuspend { fixture.declineAppointmentRequest(any(), any(), any()) } throws
            TestException()
        val sut = presentedSutWith(fixture, AppointmentRequest.stub())
        sut.uiState.requests.items.single().declineButton.onClick?.invoke()
        val dialog = sut.uiState.notifications.assertSingle<PresentationNotification.InputMessage>()
        sut.uiState.notifications.removeFirst()

        whenn()
        dialog.onConfirm("reason")

        then()
        assertTrue(
            sut.uiState.notifications.presentationNotification.none {
                it is PresentationNotification.SuccessHaptic
            },
        )
    }

    @Test
    fun `shows mapped error when decline fails`() = runUnitTest {
        given()
        val fixture = Fixture()
        everySuspend { fixture.declineAppointmentRequest(any(), any(), any()) } throws
            TestException()
        val sut = presentedSutWith(fixture, AppointmentRequest.stub())
        sut.uiState.requests.items.single().declineButton.onClick?.invoke()
        val dialog = sut.uiState.notifications.assertSingle<PresentationNotification.InputMessage>()
        sut.uiState.notifications.removeFirst()

        whenn()
        dialog.onConfirm("reason")

        then()
        fixture.errorMapper.assertMappedSingle(TestException::class)
        assertEquals(1, sut.uiState.requests.items.size)
    }

    @Test
    fun `shows mapped error when request observation fails`() = runUnitTest {
        given()
        val fixture = Fixture()
        every { fixture.getAssignedAppointmentRequests.flow(any()) } returns failOnceThenSuspend()

        whenn()
        fixture.sut()

        then()
        fixture.errorMapper.assertMappedSingle(TestException::class)
    }
}
