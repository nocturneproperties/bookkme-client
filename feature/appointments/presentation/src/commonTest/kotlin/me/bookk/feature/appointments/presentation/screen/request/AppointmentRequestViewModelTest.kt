package me.bookk.feature.appointments.presentation.screen.request

import dev.mokkery.answering.returns
import dev.mokkery.answering.throws
import dev.mokkery.every
import dev.mokkery.everySuspend
import dev.mokkery.matcher.any
import dev.mokkery.mock
import dev.mokkery.verifySuspend
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.advanceUntilIdle
import library.money.api.Money
import me.bookk.core.presentation.VmArgs
import me.bookk.core.presentation.error.PresentationNotification
import me.bookk.core.test.given
import me.bookk.core.test.runUnitTest
import me.bookk.core.test.whenn
import me.bookk.designsystem.test.FakeDateLocalizer
import me.bookk.designsystem.test.FakeErrorMapper
import me.bookk.designsystem.test.TestException
import me.bookk.designsystem.test.ViewModelTestDispatchers
import me.bookk.designsystem.test.assertMappedSingle
import me.bookk.designsystem.test.assertSingle
import me.bookk.designsystem.test.failOnceThenSuspend
import me.bookk.feature.appointments.domain.api.ApproveAppointmentRequest
import me.bookk.feature.appointments.domain.api.DeclineAppointmentRequest
import me.bookk.feature.appointments.domain.api.GetAppointmentRequests
import me.bookk.feature.appointments.domain.api.ObserveCurrentBusinessId
import me.bookk.feature.appointments.domain.api.entity.Appointment
import me.bookk.feature.appointments.domain.api.entity.AppointmentRequest
import me.bookk.feature.appointments.domain.api.entity.ServiceSnapshot
import me.bookk.feature.appointments.presentation.FakeAppointmentsStateFactory
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
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
        val getAppointmentRequests = mock<GetAppointmentRequests> {
            every { flow(any()) } returns requests
            everySuspend { refresh(any()) } returns emptyList()
        }
        val approveAppointmentRequest = mock<ApproveAppointmentRequest>()
        val declineAppointmentRequest = mock<DeclineAppointmentRequest>()
        val observeCurrentBusinessId = mock<ObserveCurrentBusinessId> {
            every { invoke() } returns MutableStateFlow<Uuid?>(businessId)
        }
        val errorMapper = FakeErrorMapper()

        fun sut() = AppointmentRequestViewModel(
            getAppointmentRequests = getAppointmentRequests,
            approveAppointmentRequest = approveAppointmentRequest,
            declineAppointmentRequest = declineAppointmentRequest,
            observeCurrentBusinessId = observeCurrentBusinessId,
            stateFactory = FakeAppointmentsStateFactory(),
            dateLocalizer = FakeDateLocalizer(),
            vmArgs = VmArgs(errorMapper)
        )
    }

    private suspend fun kotlinx.coroutines.test.TestScope.presentedSutWith(
        fixture: Fixture,
        vararg requests: AppointmentRequest
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
        verifySuspend { fixture.getAppointmentRequests.refresh(fixture.businessId) }
    }

    @Test
    fun `renders request with services and duration in minutes`() = runUnitTest {
        given()
        val fixture = Fixture()
        val service = ServiceSnapshot(Uuid.random(), "Haircut", Uuid.random(), Money(1000L, Money.SupportedCurrency.UAH), 45.minutes)
        val request = AppointmentRequest.stub().copy(services = listOf(service), note = "Window")

        whenn()
        val sut = presentedSutWith(fixture, request)

        then()
        val item = sut.uiState.requests.items.single()
        assertEquals(request.id.toString(), item.id)
        assertEquals("Haircut · 45 min", item.serviceName)
        assertEquals("Window", item.note)
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
    fun `shows message when approved slot overlaps`() = runUnitTest {
        given()
        val fixture = Fixture()
        everySuspend { fixture.approveAppointmentRequest(any()) } throws ApproveAppointmentRequest.Error.AppointmentExists()
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
        everySuspend { fixture.approveAppointmentRequest(any()) } throws ApproveAppointmentRequest.Error.DateInPast()
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
        everySuspend { fixture.approveAppointmentRequest(any()) } throws ApproveAppointmentRequest.Error.DateNotAllowed()
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
        everySuspend { fixture.approveAppointmentRequest(any()) } throws ApproveAppointmentRequest.Error.TimeNotAllowed()
        val sut = presentedSutWith(fixture, AppointmentRequest.stub())

        whenn()
        sut.uiState.requests.items.single().approveButton.onClick?.invoke()

        then()
        sut.uiState.notifications.assertSingle<PresentationNotification.Message>()
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
        verifySuspend { fixture.declineAppointmentRequest(request.id, fixture.businessId, "Fully booked") }
        assertTrue(sut.uiState.requests.items.isEmpty())
    }

    @Test
    fun `shows mapped error when decline fails`() = runUnitTest {
        given()
        val fixture = Fixture()
        everySuspend { fixture.declineAppointmentRequest(any(), any(), any()) } throws TestException()
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
        every { fixture.getAppointmentRequests.flow(any()) } returns failOnceThenSuspend()

        whenn()
        fixture.sut()

        then()
        fixture.errorMapper.assertMappedSingle(TestException::class)
    }
}
