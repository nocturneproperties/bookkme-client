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
import kotlinx.coroutines.flow.MutableStateFlow
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
import me.bookk.designsystem.test.FakeTextFieldState
import me.bookk.designsystem.uistate.ValidationState
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
import me.bookk.feature.appointments.domain.api.GetAppointmentOptions
import me.bookk.feature.appointments.domain.api.ObserveAppointmentOptions
import me.bookk.feature.appointments.domain.api.MarkAppointmentNoShow
import me.bookk.feature.appointments.domain.api.UpdateAppointment
import me.bookk.feature.appointments.domain.api.entity.Appointment
import me.bookk.feature.appointments.domain.api.entity.AppointmentCompletedBy
import me.bookk.feature.appointments.domain.api.entity.AppointmentOptions
import me.bookk.feature.appointments.domain.api.entity.AppointmentStatus
import me.bookk.feature.appointments.domain.api.entity.ClientSnapshot
import me.bookk.feature.appointments.domain.api.entity.PriceAdjustment
import me.bookk.feature.appointments.domain.api.entity.PriceAdjustmentDraft
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
        val polish = ServiceSnapshot(Uuid.random(), "Polish", Uuid.random(), Money(2550L, Money.SupportedCurrency.USD), 30.minutes)
        val massage = ServiceSnapshot(Uuid.random(), "Massage", Uuid.random(), Money(1000L, Money.SupportedCurrency.USD), 15.minutes)
        val options = MutableStateFlow(AppointmentOptions(clients = emptyList(), services = listOf(polish, massage)))
        val observeAppointmentOptions = mock<ObserveAppointmentOptions> {
            every { invoke() } returns options
        }
        val getAppointmentOptions = mock<GetAppointmentOptions> {
            everySuspend { invoke(any()) } returns options.value
        }
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
            getAppointmentOptions = getAppointmentOptions,
            observeAppointmentOptions = observeAppointmentOptions,
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

    private fun AppointmentDetailsViewModel.confirmCompletion() {
        uiState.completeButton.onClick?.invoke()
        uiState.completion.confirmButton.onClick?.invoke()
    }

    private fun AppointmentDetailsViewModel.pickServices(vararg services: ServiceSnapshot) {
        val picker = uiState.completion.servicePicker
        picker.onItemsPicked(picker.options.filter { option -> services.any { it == option.item } })
    }

    private fun AppointmentDetailsViewModel.removeServices(vararg services: ServiceSnapshot) {
        val picker = uiState.completion.servicePicker
        picker.onItemsRemoveRequested(picker.selectedItems.filter { option -> services.any { it == option.item } })
    }

    private fun AppointmentDetailsViewModel.typePrice(text: String) {
        (uiState.completion.price as FakeTextFieldState).type(text)
    }

    private fun AppointmentDetailsViewModel.typeNote(text: String) {
        (uiState.completion.note as FakeTextFieldState).type(text)
    }

    private fun AppointmentDetailsViewModel.infoTitles() = uiState.infoSections.items.map { it.title }

    private fun AppointmentDetailsViewModel.infoValue(title: dev.icerock.moko.resources.StringResource) =
        uiState.infoSections.items.single { it.title == title.desc() }.value

    @Test
    fun `renders upcoming scheduled appointment with cancel action and reschedule`() = runUnitTest {
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
    fun `hides reschedule for scheduled appointment that has started`() = runUnitTest {
        given()
        val fixture = Fixture()

        whenn()
        val sut = fixture.sutWith(fixture.startedAppointment())

        then()
        assertFalse(sut.uiState.rescheduleButton.isVisible)
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
        sut.confirmCompletion()

        then()
        assertEquals(ColorToken.Success, sut.uiState.status.color)
        assertFalse(sut.uiState.completeButton.isVisible)
        assertFalse(sut.uiState.completion.confirmButton.isLoading)
        assertFalse(sut.uiState.completion.isSheetVisible)
        assertTrue(sut.uiState.appBar.actions.items.isEmpty())
        assertFalse(sut.uiState.rescheduleButton.isVisible)
    }

    @Test
    fun `opens completion sheet instead of completing right away`() = runUnitTest {
        given()
        val fixture = Fixture()
        val sut = fixture.sutWith(fixture.startedAppointment())

        whenn()
        sut.uiState.completeButton.onClick?.invoke()

        then()
        assertTrue(sut.uiState.completion.isSheetVisible)
        verifySuspend(VerifyMode.not) { fixture.completeAppointment(any(), any()) }
    }

    @Test
    fun `offers business services as the final services list in the completion sheet`() = runUnitTest {
        given()
        val fixture = Fixture()
        val sut = fixture.sutWith(fixture.startedAppointment())

        whenn()
        sut.uiState.completeButton.onClick?.invoke()

        then()
        assertEquals(listOf(fixture.polish, fixture.massage), sut.uiState.completion.servicePicker.options.map { it.item })
        assertTrue(sut.uiState.completion.servicePicker.selectedItems.isEmpty())
    }

    @Test
    fun `explains that picked services replace the booked ones`() = runUnitTest {
        given()
        val fixture = Fixture()

        whenn()
        val sut = fixture.sutWith(fixture.startedAppointment())

        then()
        assertEquals(AppointmentsRes.strings.appointments_create_services.desc(), sut.uiState.completion.servicePicker.pickerTitle)
        assertEquals(AppointmentsRes.strings.appointments_complete_services_hint.desc(), sut.uiState.completion.servicesHint)
    }

    @Test
    fun `refreshes service options only on the first opening of the completion sheet`() = runUnitTest {
        given()
        val fixture = Fixture()
        val sut = fixture.sutWith(fixture.startedAppointment())
        sut.uiState.completeButton.onClick?.invoke()
        sut.uiState.completion.isSheetVisible = false

        whenn()
        sut.uiState.completeButton.onClick?.invoke()

        then()
        verifySuspend(VerifyMode.exactly(1)) { fixture.getAppointmentOptions(fixture.businessId) }
    }

    @Test
    fun `does not load service options until completion is requested`() = runUnitTest {
        given()
        val fixture = Fixture()

        whenn()
        fixture.sutWith(fixture.startedAppointment())

        then()
        verifySuspend(VerifyMode.not) { fixture.getAppointmentOptions(any()) }
        verify(VerifyMode.not) { fixture.observeAppointmentOptions() }
    }

    @Test
    fun `completes without price adjustment when all sheet fields are empty`() = runUnitTest {
        given()
        val fixture = Fixture()
        val appointment = fixture.startedAppointment()
        everySuspend { fixture.completeAppointment(any(), any()) } returns appointment.copy(status = AppointmentStatus.COMPLETED)
        val sut = fixture.sutWith(appointment)

        whenn()
        sut.confirmCompletion()

        then()
        verifySuspend { fixture.completeAppointment(fixture.appointmentId, null) }
    }

    @Test
    fun `completes without price adjustment when sheet fields contain only whitespace`() = runUnitTest {
        given()
        val fixture = Fixture()
        val appointment = fixture.startedAppointment()
        everySuspend { fixture.completeAppointment(any(), any()) } returns appointment.copy(status = AppointmentStatus.COMPLETED)
        val sut = fixture.sutWith(appointment)
        sut.uiState.completeButton.onClick?.invoke()
        sut.typePrice("  ")
        sut.typeNote("   ")

        whenn()
        sut.uiState.completion.confirmButton.onClick?.invoke()

        then()
        verifySuspend { fixture.completeAppointment(fixture.appointmentId, null) }
    }

    @Test
    fun `shows booked price without currency as price placeholder and currency as suffix`() = runUnitTest {
        given()
        val fixture = Fixture()
        val appointment = fixture.startedAppointment().copy(services = listOf(ServiceSnapshot.stub(), fixture.polish))

        whenn()
        val sut = fixture.sutWith(appointment)

        then()
        assertEquals("125.50".desc(), sut.uiState.completion.price.placeholder)
        assertEquals(appointment.services.first().price.currency.symbol().desc(), sut.uiState.completion.price.suffix)
    }

    @Test
    fun `fills price with total of picked services`() = runUnitTest {
        given()
        val fixture = Fixture()
        val sut = fixture.sutWith(fixture.startedAppointment())
        sut.uiState.completeButton.onClick?.invoke()

        whenn()
        sut.pickServices(fixture.polish, fixture.massage)

        then()
        assertEquals("35.50", sut.uiState.completion.price.text)
        assertEquals(listOf(fixture.polish, fixture.massage), sut.uiState.completion.servicePicker.selectedItems.map { it.item })
    }

    @Test
    fun `recalculates price when a picked service is removed`() = runUnitTest {
        given()
        val fixture = Fixture()
        val sut = fixture.sutWith(fixture.startedAppointment())
        sut.uiState.completeButton.onClick?.invoke()
        sut.pickServices(fixture.polish, fixture.massage)

        whenn()
        sut.removeServices(fixture.polish)

        then()
        assertEquals("10", sut.uiState.completion.price.text)
    }

    @Test
    fun `clears price once every picked service is removed`() = runUnitTest {
        given()
        val fixture = Fixture()
        val sut = fixture.sutWith(fixture.startedAppointment())
        sut.uiState.completeButton.onClick?.invoke()
        sut.pickServices(fixture.polish)

        whenn()
        sut.removeServices(fixture.polish)

        then()
        assertEquals("", sut.uiState.completion.price.text)
    }

    @Test
    fun `does not pick the same service twice`() = runUnitTest {
        given()
        val fixture = Fixture()
        val sut = fixture.sutWith(fixture.startedAppointment())
        sut.uiState.completeButton.onClick?.invoke()
        sut.pickServices(fixture.polish)

        whenn()
        sut.pickServices(fixture.polish)

        then()
        assertEquals(1, sut.uiState.completion.servicePicker.selectedItems.size)
        assertEquals("25.50", sut.uiState.completion.price.text)
    }

    @Test
    fun `completes with picked services, calculated price and note`() = runUnitTest {
        given()
        val fixture = Fixture()
        val appointment = fixture.startedAppointment()
        everySuspend { fixture.completeAppointment(any(), any()) } returns appointment.copy(status = AppointmentStatus.COMPLETED)
        val sut = fixture.sutWith(appointment)
        sut.uiState.completeButton.onClick?.invoke()
        sut.pickServices(fixture.polish)
        sut.typeNote(" Extra polish ")

        whenn()
        sut.uiState.completion.confirmButton.onClick?.invoke()

        then()
        val draft = PriceAdjustmentDraft(
            additionalServiceIds = listOf(fixture.polish.id),
            price = Money(2550L, Money.SupportedCurrency.USD),
            reason = "Extra polish"
        )
        verifySuspend { fixture.completeAppointment(fixture.appointmentId, draft) }
    }

    @Test
    fun `completes with manually entered price overriding the calculated one`() = runUnitTest {
        given()
        val fixture = Fixture()
        val appointment = fixture.startedAppointment()
        everySuspend { fixture.completeAppointment(any(), any()) } returns appointment.copy(status = AppointmentStatus.COMPLETED)
        val sut = fixture.sutWith(appointment)
        sut.uiState.completeButton.onClick?.invoke()
        sut.pickServices(fixture.polish)
        sut.typePrice("90,5")

        whenn()
        sut.uiState.completion.confirmButton.onClick?.invoke()

        then()
        val draft = PriceAdjustmentDraft(
            additionalServiceIds = listOf(fixture.polish.id),
            price = Money(9050L, Money.SupportedCurrency.USD),
            reason = null
        )
        verifySuspend { fixture.completeAppointment(fixture.appointmentId, draft) }
    }

    @Test
    fun `completes with booked price when only a note is entered`() = runUnitTest {
        given()
        val fixture = Fixture()
        val appointment = fixture.startedAppointment()
        everySuspend { fixture.completeAppointment(any(), any()) } returns appointment.copy(status = AppointmentStatus.COMPLETED)
        val sut = fixture.sutWith(appointment)
        sut.uiState.completeButton.onClick?.invoke()
        sut.typeNote("Client was late")

        whenn()
        sut.uiState.completion.confirmButton.onClick?.invoke()

        then()
        val draft = PriceAdjustmentDraft(
            additionalServiceIds = emptyList(),
            price = appointment.services.first().price,
            reason = "Client was late"
        )
        verifySuspend { fixture.completeAppointment(fixture.appointmentId, draft) }
    }

    @Test
    fun `rejects a price that is not a number`() = runUnitTest {
        given()
        val fixture = Fixture()
        val sut = fixture.sutWith(fixture.startedAppointment())
        sut.uiState.completeButton.onClick?.invoke()

        whenn()
        sut.typePrice("12a")

        then()
        assertFalse(sut.uiState.completion.price.isValid)
        assertEquals(ValidationState.ERROR, sut.uiState.completion.price.validationState)
        assertFalse(sut.uiState.completion.confirmButton.isEnabled)
    }

    @Test
    fun `rejects a negative price`() = runUnitTest {
        given()
        val fixture = Fixture()
        val sut = fixture.sutWith(fixture.startedAppointment())
        sut.uiState.completeButton.onClick?.invoke()

        whenn()
        sut.typePrice("-5")

        then()
        assertFalse(sut.uiState.completion.price.isValid)
        assertFalse(sut.uiState.completion.confirmButton.isEnabled)
    }

    @Test
    fun `accepts price again once it is corrected`() = runUnitTest {
        given()
        val fixture = Fixture()
        val sut = fixture.sutWith(fixture.startedAppointment())
        sut.uiState.completeButton.onClick?.invoke()
        sut.typePrice("12a")

        whenn()
        sut.typePrice("12")

        then()
        assertTrue(sut.uiState.completion.price.isValid)
        assertNull(sut.uiState.completion.price.supportingTextRes)
        assertTrue(sut.uiState.completion.confirmButton.isEnabled)
    }

    @Test
    fun `shows loading on confirm button while completing`() = runUnitTest {
        given()
        val fixture = Fixture()
        val appointment = fixture.startedAppointment()
        lateinit var sut: AppointmentDetailsViewModel
        var isLoadingDuringCompletion: Boolean? = null
        everySuspend { fixture.completeAppointment(any(), any()) } calls {
            isLoadingDuringCompletion = sut.uiState.completion.confirmButton.isLoading
            appointment.copy(status = AppointmentStatus.COMPLETED)
        }
        sut = fixture.sutWith(appointment)

        whenn()
        sut.confirmCompletion()

        then()
        assertEquals(true, isLoadingDuringCompletion)
    }

    @Test
    fun `shows price error and keeps sheet open when backend rejects a negative price`() = runUnitTest {
        given()
        val fixture = Fixture()
        everySuspend { fixture.completeAppointment(any(), any()) } throws CompleteAppointment.Error.NegativePrice()
        val sut = fixture.sutWith(fixture.startedAppointment())

        whenn()
        sut.confirmCompletion()

        then()
        assertTrue(sut.uiState.completion.isSheetVisible)
        assertEquals(
            AppointmentsRes.strings.appointments_complete_price_error.desc(),
            sut.uiState.completion.price.supportingTextRes
        )
        assertFalse(sut.uiState.completion.confirmButton.isEnabled)
    }

    @Test
    fun `shows note error and keeps sheet open when the note is too long`() = runUnitTest {
        given()
        val fixture = Fixture()
        everySuspend { fixture.completeAppointment(any(), any()) } throws CompleteAppointment.Error.ReasonTooLong()
        val sut = fixture.sutWith(fixture.startedAppointment())

        whenn()
        sut.confirmCompletion()

        then()
        assertTrue(sut.uiState.completion.isSheetVisible)
        assertEquals(
            AppointmentsRes.strings.appointments_complete_note_too_long_error.desc(),
            sut.uiState.completion.note.supportingTextRes
        )
    }

    @Test
    fun `clears note error once the note is edited`() = runUnitTest {
        given()
        val fixture = Fixture()
        everySuspend { fixture.completeAppointment(any(), any()) } throws CompleteAppointment.Error.ReasonTooLong()
        val sut = fixture.sutWith(fixture.startedAppointment())
        sut.confirmCompletion()

        whenn()
        sut.typeNote("short")

        then()
        assertNull(sut.uiState.completion.note.supportingTextRes)
    }

    @Test
    fun `closes sheet and shows message when a picked service no longer exists`() = runUnitTest {
        given()
        val fixture = Fixture()
        everySuspend { fixture.completeAppointment(any(), any()) } throws CompleteAppointment.Error.ServiceNotFound()
        val sut = fixture.sutWith(fixture.startedAppointment())

        whenn()
        sut.confirmCompletion()

        then()
        assertFalse(sut.uiState.completion.isSheetVisible)
        val message = sut.uiState.notifications.assertSingle<PresentationNotification.Message>()
        assertEquals(AppointmentsRes.strings.appointments_complete_service_not_found_error.desc(), message.message)
    }

    @Test
    fun `keeps sheet input when completion fails`() = runUnitTest {
        given()
        val fixture = Fixture()
        everySuspend { fixture.completeAppointment(any(), any()) } throws TestException()
        val sut = fixture.sutWith(fixture.startedAppointment())
        sut.uiState.completeButton.onClick?.invoke()
        sut.pickServices(fixture.polish)
        sut.uiState.completion.confirmButton.onClick?.invoke()

        whenn()
        sut.uiState.completeButton.onClick?.invoke()

        then()
        assertEquals(listOf(fixture.polish), sut.uiState.completion.servicePicker.selectedItems.map { it.item })
        assertEquals("25.50", sut.uiState.completion.price.text)
    }

    @Test
    fun `renders no-show when completion finds appointment marked as no-show`() = runUnitTest {
        given()
        val fixture = Fixture()
        everySuspend { fixture.completeAppointment(any(), any()) } throws CompleteAppointment.Error.AppointmentMarkedNoShow()
        val sut = fixture.sutWith(fixture.startedAppointment())

        whenn()
        sut.confirmCompletion()

        then()
        assertEquals(ColorToken.SecondaryText, sut.uiState.status.color)
        assertFalse(sut.uiState.completeButton.isVisible)
        assertFalse(sut.uiState.completion.isSheetVisible)
    }

    @Test
    fun `renders cancelled when completion finds appointment already cancelled`() = runUnitTest {
        given()
        val fixture = Fixture()
        everySuspend { fixture.completeAppointment(any(), any()) } throws CompleteAppointment.Error.AppointmentAlreadyCancelled()
        val sut = fixture.sutWith(fixture.startedAppointment())

        whenn()
        sut.confirmCompletion()

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
        sut.confirmCompletion()

        then()
        val message = sut.uiState.notifications.assertSingle<PresentationNotification.Message>()
        assertEquals(AppointmentsRes.strings.appointments_details_not_started_error.desc(), message.message)
        assertFalse(sut.uiState.completion.isSheetVisible)
    }

    @Test
    fun `shows mapped error when completion fails unexpectedly`() = runUnitTest {
        given()
        val fixture = Fixture()
        everySuspend { fixture.completeAppointment(any(), any()) } throws TestException()
        val sut = fixture.sutWith(fixture.startedAppointment())

        whenn()
        sut.confirmCompletion()

        then()
        fixture.errorMapper.assertMappedSingle(TestException::class)
        assertFalse(sut.uiState.completion.confirmButton.isLoading)
        assertFalse(sut.uiState.completion.isSheetVisible)
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
    fun `notes manual completion next to the status`() = runUnitTest {
        given()
        val fixture = Fixture()
        val appointment = fixture.startedAppointment(status = AppointmentStatus.COMPLETED)
            .copy(completedBy = AppointmentCompletedBy.USER)

        whenn()
        val sut = fixture.sutWith(appointment)

        then()
        assertEquals(AppointmentsRes.strings.appointments_details_marked_manually.desc(), sut.uiState.statusNote)
    }

    @Test
    fun `omits status note when appointment was completed automatically`() = runUnitTest {
        given()
        val fixture = Fixture()
        val appointment = fixture.startedAppointment(status = AppointmentStatus.COMPLETED)
            .copy(completedBy = AppointmentCompletedBy.SYSTEM)

        whenn()
        val sut = fixture.sutWith(appointment)

        then()
        assertNull(sut.uiState.statusNote)
    }

    @Test
    fun `notes manual completion once the user completes the appointment`() = runUnitTest {
        given()
        val fixture = Fixture()
        val appointment = fixture.startedAppointment()
        everySuspend { fixture.completeAppointment(any(), any()) } returns appointment.copy(
            status = AppointmentStatus.COMPLETED,
            completedBy = AppointmentCompletedBy.USER
        )
        val sut = fixture.sutWith(appointment)
        val titlesBeforeCompletion = sut.infoTitles()

        whenn()
        sut.confirmCompletion()

        then()
        assertEquals(AppointmentsRes.strings.appointments_details_marked_manually.desc(), sut.uiState.statusNote)
        assertEquals(titlesBeforeCompletion, sut.infoTitles())
    }

    private fun Fixture.adjustedAppointment(
        services: List<ServiceSnapshot> = listOf(polish),
        price: Money = Money(12000L, Money.SupportedCurrency.USD),
        reason: String? = null
    ) = startedAppointment(status = AppointmentStatus.COMPLETED).copy(
        priceAdjustment = PriceAdjustment(services, price, reason)
    )

    @Test
    fun `titles booked services as initial services when price was adjusted`() = runUnitTest {
        given()
        val fixture = Fixture()

        whenn()
        val sut = fixture.sutWith(fixture.adjustedAppointment())

        then()
        assertEquals(AppointmentsRes.strings.appointments_details_initial_services.desc(), sut.uiState.servicePicker.pickerTitle)
    }

    @Test
    fun `titles booked services as services when price adjustment has no services`() = runUnitTest {
        given()
        val fixture = Fixture()

        whenn()
        val sut = fixture.sutWith(fixture.adjustedAppointment(services = emptyList()))

        then()
        assertEquals(AppointmentsRes.strings.appointments_create_services.desc(), sut.uiState.servicePicker.pickerTitle)
    }

    @Test
    fun `titles booked services as services without price adjustment`() = runUnitTest {
        given()
        val fixture = Fixture()

        whenn()
        val sut = fixture.sutWith(fixture.startedAppointment(status = AppointmentStatus.COMPLETED))

        then()
        assertEquals(AppointmentsRes.strings.appointments_create_services.desc(), sut.uiState.servicePicker.pickerTitle)
    }

    @Test
    fun `shows price adjustment section when price was adjusted`() = runUnitTest {
        given()
        val fixture = Fixture()

        whenn()
        val sut = fixture.sutWith(fixture.adjustedAppointment())

        then()
        assertTrue(sut.uiState.priceAdjustment.isVisible)
        assertEquals(AppointmentsRes.strings.appointments_details_adjusted_services.desc(), sut.uiState.priceAdjustment.servicePicker.pickerTitle)
    }

    @Test
    fun `shows price adjustment reason with its own title`() = runUnitTest {
        given()
        val fixture = Fixture()

        whenn()
        val sut = fixture.sutWith(fixture.adjustedAppointment(reason = "Extra polish"))

        then()
        assertEquals(AppointmentsRes.strings.appointments_details_price_adjustment_reason.desc(), sut.uiState.priceAdjustment.reasonTitle)
        assertEquals("Extra polish".desc(), sut.uiState.priceAdjustment.reason)
    }

    @Test
    fun `omits price adjustment reason when it is blank`() = runUnitTest {
        given()
        val fixture = Fixture()

        whenn()
        val sut = fixture.sutWith(fixture.adjustedAppointment(reason = "  "))

        then()
        assertNull(sut.uiState.priceAdjustment.reason)
    }

    @Test
    fun `shows provided services in a read-only picker of the price adjustment section`() = runUnitTest {
        given()
        val fixture = Fixture()

        whenn()
        val sut = fixture.sutWith(fixture.adjustedAppointment(services = listOf(fixture.polish, fixture.massage)))

        then()
        val picker = sut.uiState.priceAdjustment.servicePicker
        assertTrue(picker.isVisible)
        assertFalse(picker.isEditable)
        assertEquals(listOf(fixture.polish, fixture.massage), picker.selectedItems.map { it.item })
    }

    @Test
    fun `gives every provided service its own picker id when a service is provided twice`() = runUnitTest {
        given()
        val fixture = Fixture()

        whenn()
        val sut = fixture.sutWith(fixture.adjustedAppointment(services = listOf(fixture.polish, fixture.polish)))

        then()
        assertEquals(2, sut.uiState.priceAdjustment.servicePicker.selectedItems.map { it.pickerItemId }.toSet().size)
    }

    @Test
    fun `shows adjusted price as total with booked total as original price`() = runUnitTest {
        given()
        val fixture = Fixture()
        val price = Money(12000L, Money.SupportedCurrency.USD)
        val appointment = fixture.adjustedAppointment(price = price)

        whenn()
        val sut = fixture.sutWith(appointment)

        then()
        assertEquals(price.toString(), sut.uiState.totalPrice)
        assertEquals(appointment.total, sut.uiState.originalPrice)
    }

    @Test
    fun `omits original price when adjusted price equals booked total`() = runUnitTest {
        given()
        val fixture = Fixture()
        val bookedTotal = fixture.startedAppointment().services.map { it.price }.reduce { acc, money -> acc + money }

        whenn()
        val sut = fixture.sutWith(fixture.adjustedAppointment(price = bookedTotal))

        then()
        assertEquals(bookedTotal.toString(), sut.uiState.totalPrice)
        assertNull(sut.uiState.originalPrice)
    }

    @Test
    fun `hides price adjustment section when booked services were kept`() = runUnitTest {
        given()
        val fixture = Fixture()

        whenn()
        val sut = fixture.sutWith(fixture.adjustedAppointment(services = emptyList()))

        then()
        assertFalse(sut.uiState.priceAdjustment.isVisible)
        assertFalse(sut.uiState.priceAdjustment.servicePicker.isVisible)
    }

    @Test
    fun `shows price adjustment reason when booked services were kept`() = runUnitTest {
        given()
        val fixture = Fixture()

        whenn()
        val sut = fixture.sutWith(fixture.adjustedAppointment(services = emptyList(), reason = "Extra polish"))

        then()
        assertEquals("Extra polish".desc(), sut.uiState.priceAdjustment.reason)
    }

    @Test
    fun `keeps price adjustment out of the info lines`() = runUnitTest {
        given()
        val fixture = Fixture()

        whenn()
        val sut = fixture.sutWith(fixture.adjustedAppointment(reason = "Extra polish"))

        then()
        assertFalse(AppointmentsRes.strings.appointments_details_final_price.desc() in sut.infoTitles())
        assertFalse(AppointmentsRes.strings.appointments_details_price_adjustment_reason.desc() in sut.infoTitles())
    }

    @Test
    fun `shows price adjustment section once the appointment is completed with an adjustment`() = runUnitTest {
        given()
        val fixture = Fixture()
        val appointment = fixture.startedAppointment()
        val price = Money(2550L, Money.SupportedCurrency.USD)
        everySuspend { fixture.completeAppointment(any(), any()) } returns appointment.copy(
            status = AppointmentStatus.COMPLETED,
            priceAdjustment = PriceAdjustment(listOf(fixture.polish), price, null)
        )
        val sut = fixture.sutWith(appointment)

        whenn()
        sut.confirmCompletion()

        then()
        assertTrue(sut.uiState.priceAdjustment.isVisible)
        assertEquals(AppointmentsRes.strings.appointments_details_initial_services.desc(), sut.uiState.servicePicker.pickerTitle)
        assertEquals(price.toString(), sut.uiState.totalPrice)
    }

    @Test
    fun `omits price adjustment without an adjustment`() = runUnitTest {
        given()
        val fixture = Fixture()

        whenn()
        val sut = fixture.sutWith(fixture.startedAppointment())

        then()
        assertFalse(sut.uiState.priceAdjustment.isVisible)
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
    fun `shows booked total without original price when price was not adjusted`() = runUnitTest {
        given()
        val fixture = Fixture()
        val appointment = fixture.appointment().copy(services = listOf(ServiceSnapshot.stub(), ServiceSnapshot.stub()))

        whenn()
        val sut = fixture.sutWith(appointment)

        then()
        assertEquals(AppointmentsRes.strings.appointments_details_total.desc(), sut.uiState.totalLabel)
        assertEquals(appointment.total, sut.uiState.totalPrice)
        assertNull(sut.uiState.originalPrice)
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
