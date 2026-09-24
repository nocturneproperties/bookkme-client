package me.bookk.feature.appointments.presentation.screen.details

import dev.icerock.moko.resources.desc.desc
import dev.mokkery.answering.calls
import dev.mokkery.answering.returns
import dev.mokkery.answering.throws
import dev.mokkery.every
import dev.mokkery.everySuspend
import dev.mokkery.matcher.any
import dev.mokkery.mock
import dev.mokkery.verify
import dev.mokkery.verifySuspend
import kotlinx.datetime.LocalDateTime
import library.device.api.DeviceFacade
import me.bookk.core.presentation.VmArgs
import me.bookk.core.presentation.error.PresentationNotification
import me.bookk.core.test.given
import me.bookk.core.test.runUnitTest
import me.bookk.core.test.whenn
import me.bookk.designsystem.resources.color.ColorToken
import me.bookk.designsystem.test.FakeDateLocalizer
import me.bookk.designsystem.test.FakeErrorMapper
import me.bookk.designsystem.test.TestException
import me.bookk.designsystem.test.ViewModelTestDispatchers
import me.bookk.designsystem.test.assertMappedSingle
import me.bookk.designsystem.test.assertSingle
import me.bookk.feature.appointments.domain.api.CancelAppointment
import me.bookk.feature.appointments.domain.api.GetAppointment
import me.bookk.feature.appointments.domain.api.UpdateAppointment
import me.bookk.feature.appointments.domain.api.entity.Appointment
import me.bookk.feature.appointments.domain.api.entity.AppointmentStatus
import me.bookk.feature.appointments.domain.api.entity.ClientSnapshot
import me.bookk.feature.appointments.presentation.FakeAppointmentsStateFactory
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import kotlin.uuid.Uuid

class AppointmentDetailsViewModelTest {

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
        val appointmentId = Uuid.random()
        val businessId = Uuid.random()
        val getAppointment = mock<GetAppointment>()
        val cancelAppointment = mock<CancelAppointment>()
        val updateAppointment = mock<UpdateAppointment>()
        val device = mock<DeviceFacade> {
            every { dial(any()) } returns Unit
            every { mail(any()) } returns Unit
        }
        val errorMapper = FakeErrorMapper()

        fun appointment(
            status: AppointmentStatus = AppointmentStatus.SCHEDULED,
            client: ClientSnapshot = ClientSnapshot.stub()
        ) = Appointment.stub(id = appointmentId, businessId = businessId, date = LocalDateTime(2030, 1, 1, 10, 0))
            .copy(status = status, client = client)

        fun sut() = AppointmentDetailsViewModel(
            appointmentId = appointmentId,
            getAppointment = getAppointment,
            cancelAppointment = cancelAppointment,
            updateAppointment = updateAppointment,
            dateLocalizer = FakeDateLocalizer(),
            device = device,
            stateFactory = FakeAppointmentsStateFactory(),
            vmArgs = VmArgs(errorMapper)
        )

        fun sutWith(appointment: Appointment): AppointmentDetailsViewModel {
            everySuspend { getAppointment(any()) } returns appointment
            return sut()
        }
    }

    private fun AppointmentDetailsViewModel.confirmCancellation(reason: String) {
        uiState.appBar.actions.items.single().onClick()
        val dialog = uiState.notifications.assertSingle<PresentationNotification.InputMessage>()
        uiState.notifications.removeFirst()
        dialog.onConfirm(reason)
    }

    @Test
    fun `renders scheduled appointment with cancel action and reschedule`() = runUnitTest {
        given()
        val fixture = Fixture()
        val appointment = fixture.appointment()

        whenn()
        val sut = fixture.sutWith(appointment)

        then()
        assertEquals(appointment.client.fullName.desc(), sut.uiState.appBar.title)
        assertEquals(ColorToken.ActionText, sut.uiState.status.color)
        assertEquals(1, sut.uiState.appBar.actions.items.size)
        assertTrue(sut.uiState.rescheduleButton.isVisible)
        assertEquals(appointment.date, sut.uiState.dateTimePicker.pickedDate)
    }

    @Test
    fun `hides cancel and reschedule for completed appointment`() = runUnitTest {
        given()
        val fixture = Fixture()

        whenn()
        val sut = fixture.sutWith(fixture.appointment(status = AppointmentStatus.COMPLETED))

        then()
        assertTrue(sut.uiState.appBar.actions.items.isEmpty())
        assertFalse(sut.uiState.rescheduleButton.isVisible)
        assertEquals(ColorToken.Success, sut.uiState.status.color)
    }

    @Test
    fun `omits contact lines when client has no phone or email`() = runUnitTest {
        given()
        val fixture = Fixture()
        val client = ClientSnapshot(Uuid.random(), "No Contacts", phone = null, email = "")

        whenn()
        val sut = fixture.sutWith(fixture.appointment(client = client).copy(note = ""))

        then()
        assertEquals(3, sut.uiState.infoSections.items.size)
    }

    @Test
    fun `dials client from phone line`() = runUnitTest {
        given()
        val fixture = Fixture()
        val client = ClientSnapshot(Uuid.random(), "Anna", phone = "+380501", email = null)
        val sut = fixture.sutWith(fixture.appointment(client = client))

        whenn()
        sut.uiState.infoSections.items.first().onClick?.invoke()

        then()
        verify { fixture.device.dial("+380501") }
    }

    @Test
    fun `cancels appointment with entered reason`() = runUnitTest {
        given()
        val fixture = Fixture()
        val appointment = fixture.appointment()
        everySuspend { fixture.cancelAppointment(any(), any(), any()) } returns appointment.copy(status = AppointmentStatus.CANCELLED)
        val sut = fixture.sutWith(appointment)

        whenn()
        sut.confirmCancellation("Client asked")

        then()
        verifySuspend { fixture.cancelAppointment(fixture.appointmentId, fixture.businessId, "Client asked") }
        assertEquals(ColorToken.Error, sut.uiState.status.color)
        assertFalse(sut.uiState.rescheduleButton.isVisible)
    }

    @Test
    fun `renders cancelled when appointment was already cancelled`() = runUnitTest {
        given()
        val fixture = Fixture()
        everySuspend { fixture.cancelAppointment(any(), any(), any()) } throws CancelAppointment.Error.AppointmentAlreadyCancelled()
        val sut = fixture.sutWith(fixture.appointment())

        whenn()
        sut.confirmCancellation("reason")

        then()
        assertEquals(ColorToken.Error, sut.uiState.status.color)
    }

    @Test
    fun `renders completed when appointment was already completed`() = runUnitTest {
        given()
        val fixture = Fixture()
        everySuspend { fixture.cancelAppointment(any(), any(), any()) } throws CancelAppointment.Error.AppointmentAlreadyCompleted()
        val sut = fixture.sutWith(fixture.appointment())

        whenn()
        sut.confirmCancellation("reason")

        then()
        assertEquals(ColorToken.Success, sut.uiState.status.color)
    }

    @Test
    fun `shows mapped error when cancellation fails unexpectedly`() = runUnitTest {
        given()
        val fixture = Fixture()
        everySuspend { fixture.cancelAppointment(any(), any(), any()) } throws TestException()
        val sut = fixture.sutWith(fixture.appointment())

        whenn()
        sut.confirmCancellation("reason")

        then()
        fixture.errorMapper.assertMappedSingle(TestException::class)
        sut.uiState.notifications.assertSingle<PresentationNotification.GlobalMessage>()
    }

    @Test
    fun `opens date picker on reschedule click`() = runUnitTest {
        given()
        val fixture = Fixture()
        val sut = fixture.sutWith(fixture.appointment())

        whenn()
        sut.uiState.rescheduleButton.onClick?.invoke()

        then()
        assertTrue(sut.uiState.dateTimePicker.isDatePickerVisible)
    }

    @Test
    fun `reschedules appointment to picked date`() = runUnitTest {
        given()
        val fixture = Fixture()
        val newDate = LocalDateTime(2030, 2, 1, 12, 0)
        val appointment = fixture.appointment()
        everySuspend { fixture.updateAppointment(any()) } calls { (updated: Appointment) -> updated }
        val sut = fixture.sutWith(appointment)

        whenn()
        sut.uiState.dateTimePicker.onDatePicked?.invoke(newDate)

        then()
        verifySuspend { fixture.updateAppointment(appointment.copy(date = newDate)) }
        assertEquals(newDate, sut.uiState.dateTimePicker.pickedDate)
        assertFalse(sut.uiState.rescheduleButton.isLoading)
    }

    @Test
    fun `shows message when rescheduled slot overlaps`() = runUnitTest {
        given()
        val fixture = Fixture()
        everySuspend { fixture.updateAppointment(any()) } throws UpdateAppointment.Error.AppointmentOverlap()
        val sut = fixture.sutWith(fixture.appointment())

        whenn()
        sut.uiState.dateTimePicker.onDatePicked?.invoke(LocalDateTime(2030, 2, 1, 12, 0))

        then()
        sut.uiState.notifications.assertSingle<PresentationNotification.Message>()
    }

    @Test
    fun `shows message when rescheduled date is not allowed`() = runUnitTest {
        given()
        val fixture = Fixture()
        everySuspend { fixture.updateAppointment(any()) } throws UpdateAppointment.Error.DateIsNotAllowed()
        val sut = fixture.sutWith(fixture.appointment())

        whenn()
        sut.uiState.dateTimePicker.onDatePicked?.invoke(LocalDateTime(2030, 2, 1, 12, 0))

        then()
        sut.uiState.notifications.assertSingle<PresentationNotification.Message>()
    }

    @Test
    fun `shows message when rescheduled time is not allowed`() = runUnitTest {
        given()
        val fixture = Fixture()
        everySuspend { fixture.updateAppointment(any()) } throws UpdateAppointment.Error.TimeIsNotAllowed()
        val sut = fixture.sutWith(fixture.appointment())

        whenn()
        sut.uiState.dateTimePicker.onDatePicked?.invoke(LocalDateTime(2030, 2, 1, 12, 0))

        then()
        sut.uiState.notifications.assertSingle<PresentationNotification.Message>()
    }

    @Test
    fun `shows mapped error when appointment cannot be loaded`() = runUnitTest {
        given()
        val fixture = Fixture()
        everySuspend { fixture.getAppointment(any()) } throws TestException()

        whenn()
        fixture.sut()

        then()
        fixture.errorMapper.assertMappedSingle(TestException::class)
    }

    @Test
    fun `pushes back destination on back click`() = runUnitTest {
        given()
        val fixture = Fixture()
        val sut = fixture.sutWith(fixture.appointment())

        whenn()
        sut.uiState.appBar.onBackClick?.invoke()

        then()
        assertEquals(listOf<AppointmentDetailsDestination>(AppointmentDetailsDestination.Back), sut.uiState.navigation.navigationDestination)
    }
}
