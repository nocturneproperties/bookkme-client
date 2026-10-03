package me.bookk.feature.appointments.presentation.screen.details

import dev.icerock.moko.resources.desc.desc
import dev.icerock.moko.resources.StringResource
import dev.icerock.moko.resources.format
import dev.mokkery.answering.calls
import dev.mokkery.answering.returns
import dev.mokkery.answering.throws
import dev.mokkery.every
import dev.mokkery.everySuspend
import dev.mokkery.matcher.any
import dev.mokkery.mock
import dev.mokkery.verify
import dev.mokkery.verify.VerifyMode
import dev.mokkery.verifySuspend
import kotlinx.datetime.LocalDateTime
import library.device.api.DeviceFacade
import library.money.api.Money
import me.bookk.core.presentation.VmArgs
import me.bookk.core.presentation.error.ActionType
import me.bookk.core.presentation.error.PresentationNotification
import me.bookk.core.test.given
import me.bookk.core.test.runUnitTest
import me.bookk.core.test.then
import me.bookk.core.test.whenn
import me.bookk.designsystem.resources.DesignSystem
import me.bookk.designsystem.resources.color.ColorToken
import me.bookk.designsystem.test.FakeDateLocalizer
import me.bookk.designsystem.test.FakeErrorMapper
import me.bookk.designsystem.test.TestException
import me.bookk.designsystem.test.ViewModelTestDispatchers
import me.bookk.designsystem.test.assertMappedSingle
import me.bookk.designsystem.test.assertSingle
import me.bookk.designsystem.test.tap
import me.bookk.android.feature.appointments.resources.AppointmentsRes
import me.bookk.feature.appointments.domain.api.CancelAppointment
import me.bookk.feature.appointments.domain.api.CompleteAppointment
import me.bookk.feature.appointments.domain.api.GetAppointment
import me.bookk.feature.appointments.domain.api.MarkAppointmentNoShow
import me.bookk.feature.appointments.domain.api.UpdateAppointment
import me.bookk.feature.appointments.domain.api.entity.Appointment
import me.bookk.feature.appointments.domain.api.entity.AppointmentCompletedBy
import me.bookk.feature.appointments.domain.api.entity.AppointmentStatus
import me.bookk.feature.appointments.domain.api.entity.ClientSnapshot
import me.bookk.feature.appointments.domain.api.entity.PriceAdjustment
import me.bookk.feature.appointments.domain.api.entity.ServiceSnapshot
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
        val completeAppointment = mock<CompleteAppointment>()
        val markAppointmentNoShow = mock<MarkAppointmentNoShow>()
        val device = mock<DeviceFacade> {
            every { dial(any()) } returns Unit
            every { mail(any()) } returns Unit
        }
        val errorMapper = FakeErrorMapper()

        fun appointment(
            status: AppointmentStatus = AppointmentStatus.SCHEDULED,
            client: ClientSnapshot = ClientSnapshot.stub(),
            date: LocalDateTime = LocalDateTime(2030, 1, 1, 10, 0)
        ) = Appointment.stub(id = appointmentId, businessId = businessId, date = date)
            .copy(status = status, client = client)

        fun startedAppointment(status: AppointmentStatus = AppointmentStatus.SCHEDULED) =
            appointment(status = status, date = LocalDateTime(2020, 1, 1, 10, 0))

        fun sut() = AppointmentDetailsViewModel(
            appointmentId = appointmentId,
            getAppointment = getAppointment,
            cancelAppointment = cancelAppointment,
            updateAppointment = updateAppointment,
            completeAppointment = completeAppointment,
            markAppointmentNoShow = markAppointmentNoShow,
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

    private fun AppointmentDetailsViewModel.action(description: StringResource) =
        uiState.appBar.actions.items.single { it.contentDescription == description.desc() }

    private fun AppointmentDetailsViewModel.cancelAction() = action(DesignSystem.strings.action_cancel)

    private fun AppointmentDetailsViewModel.actionDescriptions() =
        uiState.appBar.actions.items.map { it.contentDescription }

    private fun AppointmentDetailsViewModel.confirmCancellation(reason: String) {
        cancelAction().onClick()
        val dialog = uiState.notifications.assertSingle<PresentationNotification.InputMessage>()
        uiState.notifications.removeFirst()
        dialog.onConfirm(reason)
    }

    private fun AppointmentDetailsViewModel.infoTitles() = uiState.infoSections.items.map { it.title }

    private fun AppointmentDetailsViewModel.infoValue(title: dev.icerock.moko.resources.StringResource) =
        uiState.infoSections.items.single { it.title == title.desc() }.value

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
        assertEquals(listOf(DesignSystem.strings.action_cancel.desc()), sut.actionDescriptions())
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
        assertFalse(AppointmentsRes.strings.appointments_details_phone.desc() in sut.infoTitles())
        assertFalse(AppointmentsRes.strings.appointments_details_email.desc() in sut.infoTitles())
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
        assertTrue(sut.uiState.appBar.actions.items.isEmpty())
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
    fun `shows loading on reschedule button while rescheduling`() = runUnitTest {
        given()
        val fixture = Fixture()
        val appointment = fixture.appointment()
        lateinit var sut: AppointmentDetailsViewModel
        var isLoadingDuringUpdate: Boolean? = null
        everySuspend { fixture.updateAppointment(any()) } calls { (updated: Appointment) ->
            isLoadingDuringUpdate = sut.uiState.rescheduleButton.isLoading
            updated
        }
        sut = fixture.sutWith(appointment)

        whenn()
        sut.uiState.dateTimePicker.onDatePicked?.invoke(LocalDateTime(2030, 2, 1, 12, 0))

        then()
        assertEquals(true, isLoadingDuringUpdate)
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

    @Test
    fun `offers completion and no-show once a scheduled appointment has started`() = runUnitTest {
        given()
        val fixture = Fixture()

        whenn()
        val sut = fixture.sutWith(fixture.startedAppointment())

        then()
        assertTrue(sut.uiState.completeButton.isVisible)
        assertTrue(sut.uiState.noShowButton.isVisible)
    }

    @Test
    fun `hides completion and no-show before the appointment starts`() = runUnitTest {
        given()
        val fixture = Fixture()

        whenn()
        val sut = fixture.sutWith(fixture.appointment())

        then()
        assertFalse(sut.uiState.completeButton.isVisible)
        assertFalse(sut.uiState.noShowButton.isVisible)
    }

    @Test
    fun `completed appointment offers only no-show`() = runUnitTest {
        given()
        val fixture = Fixture()

        whenn()
        val sut = fixture.sutWith(fixture.startedAppointment(status = AppointmentStatus.COMPLETED))

        then()
        assertFalse(sut.uiState.completeButton.isVisible)
        assertTrue(sut.uiState.noShowButton.isVisible)
    }

    @Test
    fun `completes appointment and hides scheduled-only actions`() = runUnitTest {
        given()
        val fixture = Fixture()
        val appointment = fixture.startedAppointment()
        everySuspend { fixture.completeAppointment(any(), any()) } returns appointment.copy(
            status = AppointmentStatus.COMPLETED,
            completedBy = AppointmentCompletedBy.USER
        )
        val sut = fixture.sutWith(appointment)

        whenn()
        sut.uiState.completeButton.onClick?.invoke()

        then()
        verifySuspend { fixture.completeAppointment(fixture.appointmentId, null) }
        assertEquals(ColorToken.Success, sut.uiState.status.color)
        assertFalse(sut.uiState.completeButton.isVisible)
        assertFalse(sut.uiState.completeButton.isLoading)
        assertTrue(sut.uiState.appBar.actions.items.isEmpty())
        assertFalse(sut.uiState.rescheduleButton.isVisible)
    }

    @Test
    fun `renders no-show when completion finds appointment marked as no-show`() = runUnitTest {
        given()
        val fixture = Fixture()
        everySuspend { fixture.completeAppointment(any(), any()) } throws CompleteAppointment.Error.AppointmentMarkedNoShow()
        val sut = fixture.sutWith(fixture.startedAppointment())

        whenn()
        sut.uiState.completeButton.onClick?.invoke()

        then()
        assertEquals(ColorToken.SecondaryText, sut.uiState.status.color)
        assertFalse(sut.uiState.completeButton.isVisible)
    }

    @Test
    fun `renders cancelled when completion finds appointment already cancelled`() = runUnitTest {
        given()
        val fixture = Fixture()
        everySuspend { fixture.completeAppointment(any(), any()) } throws CompleteAppointment.Error.AppointmentAlreadyCancelled()
        val sut = fixture.sutWith(fixture.startedAppointment())

        whenn()
        sut.uiState.completeButton.onClick?.invoke()

        then()
        assertEquals(ColorToken.Error, sut.uiState.status.color)
        assertFalse(sut.uiState.noShowButton.isVisible)
    }

    @Test
    fun `shows message when completing an appointment that has not started`() = runUnitTest {
        given()
        val fixture = Fixture()
        everySuspend { fixture.completeAppointment(any(), any()) } throws CompleteAppointment.Error.AppointmentNotStarted()
        val sut = fixture.sutWith(fixture.startedAppointment())

        whenn()
        sut.uiState.completeButton.onClick?.invoke()

        then()
        val message = sut.uiState.notifications.assertSingle<PresentationNotification.Message>()
        assertEquals(AppointmentsRes.strings.appointments_details_not_started_error.desc(), message.message)
    }

    @Test
    fun `shows mapped error when completion fails unexpectedly`() = runUnitTest {
        given()
        val fixture = Fixture()
        everySuspend { fixture.completeAppointment(any(), any()) } throws TestException()
        val sut = fixture.sutWith(fixture.startedAppointment())

        whenn()
        sut.uiState.completeButton.onClick?.invoke()

        then()
        fixture.errorMapper.assertMappedSingle(TestException::class)
        assertFalse(sut.uiState.completeButton.isLoading)
    }

    @Test
    fun `marks appointment as no-show after confirmation`() = runUnitTest {
        given()
        val fixture = Fixture()
        val appointment = fixture.startedAppointment()
        everySuspend { fixture.markAppointmentNoShow(any()) } returns appointment.copy(status = AppointmentStatus.NO_SHOW)
        val sut = fixture.sutWith(appointment)
        sut.uiState.noShowButton.onClick?.invoke()
        val confirmation = sut.uiState.notifications.assertSingle<PresentationNotification.Message>()
        sut.uiState.notifications.removeFirst()

        whenn()
        confirmation.tap(ActionType.NEGATIVE)

        then()
        verifySuspend { fixture.markAppointmentNoShow(fixture.appointmentId) }
        assertEquals(ColorToken.SecondaryText, sut.uiState.status.color)
        assertEquals(AppointmentsRes.strings.appointments_status_no_show.desc(), sut.uiState.status.label)
        assertFalse(sut.uiState.noShowButton.isVisible)
        assertTrue(sut.uiState.appBar.actions.items.isEmpty())
    }

    @Test
    fun `does not mark no-show when confirmation is dismissed`() = runUnitTest {
        given()
        val fixture = Fixture()
        val sut = fixture.sutWith(fixture.startedAppointment())
        sut.uiState.noShowButton.onClick?.invoke()
        val confirmation = sut.uiState.notifications.assertSingle<PresentationNotification.Message>()

        whenn()
        confirmation.tap(ActionType.CANCEL)

        then()
        verifySuspend(VerifyMode.not) { fixture.markAppointmentNoShow(any()) }
    }

    @Test
    fun `renders cancelled when no-show finds appointment already cancelled`() = runUnitTest {
        given()
        val fixture = Fixture()
        everySuspend { fixture.markAppointmentNoShow(any()) } throws MarkAppointmentNoShow.Error.AppointmentAlreadyCancelled()
        val sut = fixture.sutWith(fixture.startedAppointment())
        sut.uiState.noShowButton.onClick?.invoke()
        val confirmation = sut.uiState.notifications.assertSingle<PresentationNotification.Message>()
        sut.uiState.notifications.removeFirst()

        whenn()
        confirmation.tap(ActionType.NEGATIVE)

        then()
        assertEquals(ColorToken.Error, sut.uiState.status.color)
    }

    @Test
    fun `shows who completed the appointment`() = runUnitTest {
        given()
        val fixture = Fixture()
        val appointment = fixture.startedAppointment(status = AppointmentStatus.COMPLETED)
            .copy(completedBy = AppointmentCompletedBy.SYSTEM)

        whenn()
        val sut = fixture.sutWith(appointment)

        then()
        assertEquals(
            AppointmentsRes.strings.appointments_details_completed_by_system.desc(),
            sut.infoValue(AppointmentsRes.strings.appointments_details_completed_by)
        )
    }

    @Test
    fun `shows final price, additional services and reason of a price adjustment`() = runUnitTest {
        given()
        val fixture = Fixture()
        val extra = ServiceSnapshot.stub().copy(name = "Polish")
        val price = Money(120.0, Money.SupportedCurrency.USD)
        val appointment = fixture.startedAppointment(status = AppointmentStatus.COMPLETED).copy(
            completedBy = AppointmentCompletedBy.USER,
            priceAdjustment = PriceAdjustment(listOf(extra), price, "Extra polish")
        )

        whenn()
        val sut = fixture.sutWith(appointment)

        then()
        assertEquals(price.toString().desc(), sut.infoValue(AppointmentsRes.strings.appointments_details_final_price))
        assertEquals("Polish".desc(), sut.infoValue(AppointmentsRes.strings.appointments_details_additional_services))
        assertEquals("Extra polish".desc(), sut.infoValue(AppointmentsRes.strings.appointments_details_price_adjustment_reason))
    }

    @Test
    fun `omits price adjustment lines without an adjustment`() = runUnitTest {
        given()
        val fixture = Fixture()

        whenn()
        val sut = fixture.sutWith(fixture.startedAppointment())

        then()
        assertFalse(AppointmentsRes.strings.appointments_details_final_price.desc() in sut.infoTitles())
        assertFalse(AppointmentsRes.strings.appointments_details_completed_by.desc() in sut.infoTitles())
    }

    @Test
    fun `shows message when rescheduling an appointment that is no longer scheduled`() = runUnitTest {
        given()
        val fixture = Fixture()
        everySuspend { fixture.updateAppointment(any()) } throws UpdateAppointment.Error.AppointmentNotScheduled()
        val sut = fixture.sutWith(fixture.appointment())

        whenn()
        sut.uiState.dateTimePicker.onDatePicked?.invoke(LocalDateTime(2030, 2, 1, 12, 0))

        then()
        val message = sut.uiState.notifications.assertSingle<PresentationNotification.Message>()
        assertEquals(AppointmentsRes.strings.appointments_details_not_scheduled_error.desc(), message.message)
    }

    @Test
    fun `shows message when rescheduling to a suspended employee`() = runUnitTest {
        given()
        val fixture = Fixture()
        everySuspend { fixture.updateAppointment(any()) } throws UpdateAppointment.Error.EmployeeSuspended()
        val sut = fixture.sutWith(fixture.appointment())

        whenn()
        sut.uiState.dateTimePicker.onDatePicked?.invoke(LocalDateTime(2030, 2, 1, 12, 0))

        then()
        val message = sut.uiState.notifications.assertSingle<PresentationNotification.Message>()
        assertEquals(AppointmentsRes.strings.appointments_details_employee_suspended_error.desc(), message.message)
    }

    @Test
    fun `shows booked services in a read-only picker`() = runUnitTest {
        given()
        val fixture = Fixture()
        val haircut = ServiceSnapshot.stub().copy(name = "Haircut")
        val beard = ServiceSnapshot.stub().copy(name = "Beard")
        val appointment = fixture.appointment().copy(services = listOf(haircut, beard))

        whenn()
        val sut = fixture.sutWith(appointment)

        then()
        assertEquals(listOf(haircut, beard), sut.uiState.servicePicker.selectedItems.map { it.item })
        assertFalse(sut.uiState.servicePicker.isEditable)
    }

    @Test
    fun `gives every booked service its own picker id when a service is booked twice`() = runUnitTest {
        given()
        val fixture = Fixture()
        val haircut = ServiceSnapshot.stub()
        val appointment = fixture.appointment().copy(services = listOf(haircut, haircut))

        whenn()
        val sut = fixture.sutWith(appointment)

        then()
        val ids = sut.uiState.servicePicker.selectedItems.map { it.pickerItemId }
        assertEquals(2, ids.toSet().size)
    }

    @Test
    fun `shows subtotal of booked services`() = runUnitTest {
        given()
        val fixture = Fixture()
        val appointment = fixture.appointment().copy(services = listOf(ServiceSnapshot.stub(), ServiceSnapshot.stub()))

        whenn()
        val sut = fixture.sutWith(appointment)

        then()
        assertEquals(AppointmentsRes.strings.appointments_create_subtotal.format(2), sut.uiState.subtotalLabel)
        assertEquals(appointment.total, sut.uiState.subtotalPrice)
    }

    @Test
    fun `keeps services and estimated revenue out of the info lines`() = runUnitTest {
        given()
        val fixture = Fixture()
        val appointment = fixture.appointment()

        whenn()
        val sut = fixture.sutWith(appointment)

        then()
        assertFalse(AppointmentsRes.strings.appointments_create_services.desc() in sut.infoTitles())
        assertFalse(sut.uiState.infoSections.items.any { it.value == appointment.total.desc() })
    }

    @Test
    fun `renders appointment date among the info lines`() = runUnitTest {
        given()
        val fixture = Fixture()
        val appointment = fixture.appointment()

        whenn()
        val sut = fixture.sutWith(appointment)

        then()
        assertEquals(appointment.date.toString().desc(), sut.infoValue(AppointmentsRes.strings.appointments_create_date))
    }

    @Test
    fun `renders rescheduled date among the info lines`() = runUnitTest {
        given()
        val fixture = Fixture()
        val newDate = LocalDateTime(2030, 2, 2, 12, 0)
        val appointment = fixture.appointment()
        everySuspend { fixture.updateAppointment(any()) } returns appointment.copy(date = newDate)
        val sut = fixture.sutWith(appointment)

        whenn()
        sut.uiState.dateTimePicker.onDatePicked?.invoke(newDate)

        then()
        assertEquals(newDate.toString().desc(), sut.infoValue(AppointmentsRes.strings.appointments_create_date))
    }

    @Test
    fun `hides services until the appointment is loaded`() = runUnitTest {
        given()
        val fixture = Fixture()
        everySuspend { fixture.getAppointment(any()) } throws TestException()

        whenn()
        val sut = fixture.sut()

        then()
        assertFalse(sut.uiState.servicePicker.isVisible)
    }

    @Test
    fun `shows services once the appointment is loaded`() = runUnitTest {
        given()
        val fixture = Fixture()

        whenn()
        val sut = fixture.sutWith(fixture.appointment())

        then()
        assertTrue(sut.uiState.servicePicker.isVisible)
    }
}
